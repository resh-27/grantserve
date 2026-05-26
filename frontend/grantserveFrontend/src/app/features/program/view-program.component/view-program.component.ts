import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { ProgramService } from '../service/program.service';
import { ProgramDataService } from '../service/program-data.service';
import { forkJoin, of, switchMap } from 'rxjs';
import { PagedResponse } from '../model/paged-model';
import { Chart, ArcElement, Tooltip, Legend } from 'chart.js';
import { JwtService } from '../../../core/services/jwtService/jwt-service';
import { ToastService } from '../../../shared/services/toast.service';
import { ApplicationsService } from '../../Applications/service/applications.service';

Chart.register(ArcElement, Tooltip, Legend);

@Component({
  selector: 'app-view-program',
  standalone: true,
  imports: [CommonModule, RouterModule, ManagerHeaderComponent, RouterLink],
  templateUrl: './view-program.component.html',
  styleUrls: ['./view-program.component.css'],
})
export class ViewProgramComponent implements OnInit {
  programData: any;
  appliedProgramIds: number[] = [];
  viewMode: 'manager' | 'researcher' = 'researcher'; // This could come from an Auth service
  loggedInResearcherId: number | null;

  @ViewChild('analyticsChart') chartCanvas!: ElementRef<HTMLCanvasElement>;
  chart: any;

  isLoading: boolean = true;
  isDescriptionExpanded = false;
  readonly DESCRIPTION_LIMIT = 1300;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private programService: ProgramService,
    private dataService: ProgramDataService,
    private toast: ToastService,
    private cdr: ChangeDetectorRef,
    private jwtService: JwtService,
    private applicationsService: ApplicationsService
  ) {
    this.loggedInResearcherId = jwtService.getUserId();
  }

  ngOnInit(): void {
    const storedRole = this.jwtService.getUserRole()?.toLowerCase();

    this.viewMode = storedRole === 'manager' ? 'manager' : 'researcher';

    const id = this.route.snapshot.paramMap.get('id');
    const isOnlyDigits = (str: string) => /^\d+$/.test(str);

    const userId = this.jwtService.getUserId();
    
    if (userId) {
      this.loadAppliedProgramIds(userId);
    } else {
      localStorage.removeItem('token');
      this.toast.show('Invalid user ID', 'danger');
      this.router.navigate(['/']);
    }

    if (id && isOnlyDigits(id)) {
      this.loadProgramData(id);
    } else {
      this.router.navigate(['/manager/programs']);
    }
  }

  toggleDescription() {
    this.isDescriptionExpanded = !this.isDescriptionExpanded;
  }

  shouldShowReadMore(text: string): boolean {
    return text ? text.length > this.DESCRIPTION_LIMIT : false;
  }

  goBack() {
    this.location.back();
  }

  loadAppliedProgramIds(userId: number): void {
    this.applicationsService.fetchAppliedProgramIds(userId)
      .subscribe({
        next: (ids) => {
          this.appliedProgramIds = ids;
        },
        error: (err) => {
          this.toast.show('Failed to load programs', 'danger');
          this.router.navigate(['/']);
        }
      });
  }

  private loadProgramData(id: string): void {
    this.isLoading = true;

    // 1. Determine which stream to use based on role
    const programRequest$ =
      this.viewMode === 'manager'
        ? this.programService.searchPrograms(undefined, Number(id))
        : this.programService.searchProgramsForResearcher(undefined, Number(id));

    programRequest$
      .pipe(
        switchMap((response: PagedResponse<any>) => {
          const content = response.content || [];
          if (content.length === 0) {
            this.toast.show('Program not found.', 'warning');
            this.router.navigate(['/manager/programs']);
            return of(null);
          }

          const actualProgram = content[0];
          const ids = [Number(actualProgram.programID)];

          // 2. Prepare parallel requests
          const sources: any = {
            program: of(actualProgram),
          };

          if (Number(actualProgram.budgetId) !== 0) {
            sources.budget = this.dataService.getBudgetByBudgetId(actualProgram.budgetId);
          }

          // Only fetch analytics if manager
          if (this.viewMode === 'manager') {
            sources.analytics = this.dataService.getBulkAnalytics(ids);
          }

          return forkJoin(sources);
        }),
        switchMap((results: any) => {
          if (!results) return of(null);

          const { program, budget, analytics } = results;

          // 3. Map to the specific format the HTML expects
          const mapped = {
            program: program, // This is now a single object, not an array
            isDraft: program.status?.toUpperCase() === 'DRAFT',
            hasApplied: true,
            budget: {
              spentAmount: budget?.spentAmount || 0,
              totalAmount: budget?.allocatedAmount || 0,
              remainingAmount: budget?.remainingAmount || 0,
            },
            analytics: {},
          };
          if (this.viewMode === 'manager') {
            mapped.analytics = (analytics && analytics[program.programID]) || {
              totalApplications: 0,
              acceptanceRate: 0,
              monthlyStats: { labels: [], accepted: [], rejected: [], pending: [] },
            };
          } else {
            mapped.hasApplied = this.appliedProgramIds.includes(mapped.program.programID);
          }

          return of(mapped);
        }),
      )
      .subscribe({
        next: (mappedData: any) => {
          if (!mappedData) return;

          this.programData = mappedData;
          this.isLoading = false;
          this.cdr.detectChanges();

          // 4. Initialize chart for managers if data exists
          if (
            this.viewMode === 'manager' &&
            !this.programData?.isDraft &&
            this.programData?.analytics?.totalApplications > 0
          ) {
            setTimeout(() => this.initChart());
          }
        },
        error: (err) => {
          this.isLoading = false;
          const errorMsg = err.error?.message || 'Error connecting to the program service.';
          this.toast.show(errorMsg, 'danger');
          this.router.navigate(['/manager/programs']);
          this.cdr.detectChanges();
        },
      });
  }

  initChart() {
    const stats = this.programData.analytics.monthlyStats;
    new Chart(this.chartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: stats.labels,
        datasets: [
          { label: 'Accepted', data: stats.accepted, backgroundColor: '#0d6efd', borderRadius: 4 },
          { label: 'Rejected', data: stats.rejected, backgroundColor: '#e2e8f0', borderRadius: 4 },
          { label: 'Pending', data: stats.pending, backgroundColor: '#ffc107', borderRadius: 4 },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: { boxWidth: 12, font: { family: 'Inter' } },
          },
        },
        scales: {
          x: { grid: { display: false } },
          y: { beginAtZero: true, grid: { color: '#f0f0f0', ...({ borderDash: [5, 5] } as any) } },
        },
      },
    });
  }

  deleteProgram(id: number) {
    if (confirm('Are you sure?')) {
      this.programService.deleteProgram(id).subscribe({
        next: () => {
          this.toast.show('Draft program deleted successfully!', 'success');
          this.goBack();
        },
        error: (err) => {
          // Uses the 'message' from your Java buildResponse()
          const errorMsg = err.error?.message || 'Error deleting program';
          this.toast.show(errorMsg, 'danger');
        }
      });
    }
  }
}
