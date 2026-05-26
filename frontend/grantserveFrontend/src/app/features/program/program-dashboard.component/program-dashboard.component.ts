import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FilterCriteria, ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { ProgramCardComponent } from '../program-card.component/program-card.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProgramService } from '../service/program.service';
import { ProgramDataService } from '../service/program-data.service';
import { forkJoin, of, switchMap } from 'rxjs';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
  selector: 'app-program-dashboard',
  imports: [CommonModule, FormsModule, ProgramCardComponent, ManagerHeaderComponent],
  templateUrl: './program-dashboard.component.html',
  styleUrl: './program-dashboard.component.css',
  standalone: true
})
export class ProgramDashboardComponent implements OnInit {
  allPrograms: any[] = [];
  isLoading: boolean = true;

  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;

  currentCriteria: FilterCriteria = {
    title: '',
    status: undefined,
    sortBy: 'programID',
    direction: 'desc'
  };

  constructor(
    private programService: ProgramService,
    private dataService: ProgramDataService,
    private toast: ToastService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadPrograms(this.currentCriteria, 0);
  }

  onFilterChange(criteria: FilterCriteria): void {
    this.currentCriteria = criteria;
    this.loadPrograms(criteria, 0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadPrograms(this.currentCriteria, page);
      // Scroll to top of results
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  loadPrograms(criteria: FilterCriteria, page: number): void {
    this.isLoading = true;

    this.programService.searchPrograms(
      criteria.title,
      undefined,
      criteria.status,
      criteria.startDate,
      criteria.endDate,
      page,
      this.pageSize,
      criteria.sortBy,
      criteria.direction
    ).pipe(
      // tap(response => console.log('1. Raw API Result:', response)),

      switchMap((response: any) => {
        const content = Array.isArray(response) ? response : (response?.content || []);
        if (content.length === 0) {
          this.currentPage = 0;
          this.totalPages = 0;
          return of([])
        };

        this.currentPage = response.page?.number || 0;
        this.totalPages = response.page?.totalPages || 0;

        const isDraftFilter = criteria.status === 'DRAFT';

        if (isDraftFilter) {
          const mapped = content.map((prog: any) => ({
            prog: prog,
            isDraft: true,
            budget: {
              spentAmount: 0,
              totalAmount: 0
            },
            analytics: {
              totalApplications: 0,
              acceptanceRate: 0
            }
          }));

          return of(mapped);
        }


        const ids = content.map((p: any) => Number(p.programID));

        return forkJoin({
          list: of(content),
          budgets: this.dataService.getBudgetsByProgramIds(ids),
          analytics: this.dataService.getBulkAnalytics(ids)
        }).pipe(
          switchMap(({ list, budgets, analytics }) => {
            this.currentPage = response.page?.number || 0;
            this.totalPages = response.page?.totalPages || 0;

            const mapped = list.map((prog: any) => {
              const budgetData = budgets.find((b: any) => b.programId == prog.programID);
              return {
                prog: prog,
                isDraft: prog.status?.toUpperCase() === 'DRAFT',
                budget: {
                  spentAmount: budgetData?.spentAmount || 0,
                  totalAmount: budgetData?.allocatedAmount || 0
                },
                analytics: analytics[prog.programID] || { totalApplications: 0, acceptanceRate: 0 }
              };
            });
            // console.log('Mapped Program Data:', mapped);
            return of(mapped);
          })
        );
      })
    ).subscribe({
      next: (mappedData: any) => {
        this.isLoading = false;
        this.allPrograms = mappedData;
        this.cdr.detectChanges();
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Failed to load programs. Please try again later.';
        this.isLoading = false;
        
        this.allPrograms = [];
        this.currentPage = 0;
        this.totalPages = 0;
        
        this.toast.show(errorMsg, 'danger');
        this.cdr.detectChanges();
      }
    });
  }

  onProgramDeleted(deletedId: number) {
    this.allPrograms = this.allPrograms.filter(p => p.prog.programID !== deletedId);
    this.onFilterChange(this.currentCriteria);
  }
}