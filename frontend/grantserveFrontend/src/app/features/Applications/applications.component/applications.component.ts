import { ChangeDetectorRef, Component, effect, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicationsService } from '../service/applications.service';
import { Sidebar } from '../../../shared/components/sidebar/sidebar';
import { ApplicationCard } from '../application-card/application-card';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { UserActions } from './application.action';
import { selectCounts } from './application.selector';


@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [Sidebar, CommonModule, ApplicationCard, FormsModule, RouterLink],
  templateUrl: './applications.component.html',
  styleUrl: './applications.component.css',
})
export class ApplicationsComponent implements OnInit {
  // Data lists
  filteredApplications: any[] = [];

  // Pagination State
  currentPage: number = 0;
  pageSize: number = 6;
  totalPages: number = 0;
  totalElements: number = 0;

  // Filter & Search State
  currentFilter: string = 'All';
  searchTerm: string = '';
  isSearching: boolean = false;
  statusCounts = { All: 0, Submitted: 0, 'Under Review': 0, Approved: 0, Rejected: 0 };
  readonly store = inject(Store);
  satuscountsfromstore = this.store.selectSignal(selectCounts);
  private searchSubject = new Subject<string>();


  constructor(
    private App: ApplicationsService,
    private cdr: ChangeDetectorRef
  ) {
    effect(() => {
      // This will trigger EVERY time the store data changes
      console.log('✨ STORE UPDATED IN UI:', this.satuscountsfromstore());
    });
  }

  ngOnInit() {
    this.loadApplications();
    const currentData = this.satuscountsfromstore();

    // 2. Only dispatch if the data is "empty" (initial state)
    if (currentData.All === 0 && !currentData.loading) {
      console.log('🌐 STORE EMPTY: Fetching from API...');
      this.store.dispatch(UserActions.getCount());
    } else {
      console.log('📦 STORE HAS DATA: Skipping API call, using cache!');
    }
    this.setupSearchSubscription();
  }

  /**
   * Main fetch method that handles Pagination + Filtering + Searching
   */
  loadApplications() {
    // Note: You should update your Service to accept (page, size, status, searchTerm)
    // For now, using your existing logic but wrapping it in the Page structure
    this.App.getApplications(this.currentPage, this.pageSize, this.currentFilter, this.searchTerm)
      .subscribe({
        next: (res: any) => {
          // 'res' is the Spring Page object you showed earlier
          this.filteredApplications = res.content;
          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;

          // We still need to calculate counts (usually a separate lightweight API call is better)
          // For now, we'll assume the counts are updated elsewhere or via a summary object
          this.updateMetadata(res);
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error fetching applications', err)
      });
  }

  setupSearchSubscription() {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0; // Reset to first page on new search
      this.loadApplications();
    });
  }

  // --- UI Actions ---

  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.currentPage = newPage;
      this.loadApplications();
      // Scroll to top of list for better UX
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  filterBy(status: string) {
    this.currentFilter = status;
    this.currentPage = 0; // Reset to first page on filter change
    this.loadApplications();
  }

  onSearch(event: any) {
    const value = event.target ? event.target.value : event;
    this.isSearching = value.trim().length > 0;
    this.searchSubject.next(value);
  }

  clearSearch() {
    this.searchTerm = '';
    this.isSearching = false;
    this.currentPage = 0;
    this.loadApplications();
  }

  private updateMetadata(res: any) {
    // 1. Update the 'All' count immediately from the current Page response
    if (this.currentFilter === 'All' || !this.searchTerm) {
      this.statusCounts.All = res.totalElements || 0;
    }

    // 2. Fetch the latest breakdown for all status badges from the service
    this.App.getUserApplicationCounts().subscribe({
      next: (counts: any) => {
        // This syncs all badges: Submitted, Under Review, Approved, Rejected
        console.log("fetching data from service layer : ", counts)
        this.statusCounts = { ...this.statusCounts, ...counts };
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Silent failure updating badge counts', err)
    });
  }
}