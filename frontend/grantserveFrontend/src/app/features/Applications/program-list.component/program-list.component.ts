import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProgramService } from '../../program/service/program.service';
import { ProgramCardComponent } from '../../program/program-card.component/program-card.component';
import { PagedResponse } from '../../program/model/paged-model';
import { Sidebar } from '../../../shared/components/sidebar/sidebar';
import { forkJoin, of, switchMap } from 'rxjs';
import { FilterCriteria } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { ProgramDataService } from '../../program/service/program-data.service';
import { JwtService } from '../../../core/services/jwtService/jwt-service';
import { ApplicationsService } from '../service/applications.service';
import { ToastService } from '../../../shared/services/toast.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-program-list',
  imports: [ProgramCardComponent, FormsModule, CommonModule],
  templateUrl: './program-list.component.html',
  styleUrls: ['./program-list.component.css']
})
export class ProgramListComponent implements OnInit {

  appliedProgramIds: number[] = [];
  pagedData: any[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  searchTerm: string = '';
  status: string = 'ALL';
  startDate: string = '';
  endDate: string = '';
  sortBy: string = 'programID';
  direction: string = 'desc';
  showAdvancedFilters: boolean = false;
  
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;

  currentFilters: FilterCriteria = {
    title: '',
    status: '',
    sortBy: '',
    direction: ''
  };

  constructor(
    private programService: ProgramService,
    private dataService: ProgramDataService,
    private cdr: ChangeDetectorRef,
    private jwtService: JwtService,
    private applicationsService: ApplicationsService,
    private toast: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.jwtService.getUserId();
    
    if (userId) {
      this.loadAppliedProgramIds(userId);
      this.loadPrograms(this.currentFilters);
    } else {
      localStorage.removeItem('token');
      this.toast.show('Invalid user ID', 'danger');
      this.router.navigate(['/']);
    }
  }

  applyFilters(): void {
    this.currentFilters = {
      ...this.currentFilters,
      title: this.searchTerm,
      startDate: this.startDate || undefined,
      endDate: this.endDate || undefined,
      sortBy: this.sortBy,
      direction: this.direction,
      status: this.status === "ALL" ? undefined : this.status
    };
    
    this.loadPrograms(this.currentFilters);
  }

  toggleAdvancedFilters() {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadPrograms(this.currentFilters, page);
      // Scroll to top of results
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
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

  loadPrograms(criteria: FilterCriteria, page: number = 0): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.currentPage = page;

    this.programService.searchProgramsForResearcher(
      criteria.title,
      undefined,
      criteria.status,
      criteria.startDate,
      criteria.endDate,
      this.currentPage,
      this.pageSize,
      criteria.sortBy,
      criteria.direction
    ).pipe(
      switchMap((response: PagedResponse<any>) => {
        if (!response) return of([]);

        this.currentPage = response.page?.number || 0;
        this.totalPages = response.page?.totalPages || 0;

          const content = response.content || [];
          if (content.length === 0) return of([]);
  
          const ids = content.map((p: any) => Number(p.programID));
  
          return forkJoin({
            list: of(content),
            budgets: this.dataService.getBudgetsByProgramIds(ids)
          }).pipe(
            switchMap(({ list, budgets }) => {
              const mapped = list.map((prog: any) => {
                const budgetData = budgets.find((b: any) => b.programId == prog.programID);
                return {
                  prog: prog,
                  hasApplied: this.appliedProgramIds.includes(Number(prog.programID)),
                  budget: {
                    spentAmount: budgetData?.spentAmount || 0,
                    totalAmount: budgetData?.allocatedAmount || 0
                  }
                };
              });
              return of(mapped);
            })
          );
        })
      ).subscribe({
        next: (mappedData: any) => {
          this.pagedData = mappedData;
          this.isLoading = false;
          this.cdr.detectChanges();

        console.log('Mapped Program Data:', this.pagedData);
      },
      error: (err) => {
        this.errorMessage = 'Failed to load programs. Please try again later.';
        this.pagedData = [];
        this.isLoading = false;
        this.cdr.detectChanges();
        console.error('Error loading programs:', err);
      }
    });
  }

}