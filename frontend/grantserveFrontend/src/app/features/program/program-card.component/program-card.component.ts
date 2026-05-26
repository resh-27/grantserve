import { Component, Input, AfterViewInit, ElementRef, ViewChild, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { Router, RouterLink } from '@angular/router';
import { ProgramService } from '../service/program.service';
import { ChangeDetectorRef } from '@angular/core';
import { ToastService } from '../../../shared/services/toast.service';
import { JwtService } from '../../../core/services/jwtService/jwt-service';

Chart.register(...registerables);

@Component({
  selector: 'app-program-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './program-card.component.html',
  styleUrl: './program-card.component.css',
})
export class ProgramCardComponent implements AfterViewInit {
  @Input() data: any;
  @Input() index: number = 0;
  @Input() isDraft: boolean = false;
  @Input() hasApplied: boolean = true;
  viewMode: 'manager' | 'researcher';

  @Output() programDeleted = new EventEmitter<number>();

  @ViewChild('analyticsChart') chartCanvas!: ElementRef<HTMLCanvasElement>;

  loggedInResearcherId: number | null;
  isExpanded: boolean = false;
  isTitleExpanded: boolean = false;
  readonly DESC_LIMIT = 100;
  readonly TITLE_LIMIT = 100;

  constructor(
    private toast: ToastService,
    private router: Router,
    private programService: ProgramService,
    private cdr: ChangeDetectorRef,
    private jwtService: JwtService
  ) {
    this.loggedInResearcherId = jwtService.getUserId();
    this.viewMode = jwtService.getUserRole() === 'MANAGER' ? 'manager' : 'researcher';

    if (this.loggedInResearcherId === null || this.viewMode === null) {
      router.navigate(['/']);
    }
  }

  ngAfterViewInit() {
    if (this.viewMode === 'manager' && !this.isDraft && this.data?.analytics?.monthlyStats?.labels?.length > 0) {
      this.initChart();
    }
  }

  toggleDescription() {
    this.isExpanded = !this.isExpanded;
  }

  get description(): string {
    return this.data?.prog?.description || 'No description available.';
  }

  get shouldShowReadMore(): boolean {
    return this.description.length > this.DESC_LIMIT;
  }

  toggleTitle() {
    this.isTitleExpanded = !this.isTitleExpanded;
  }

  get programTitle(): string {
    return this.data?.prog?.title || 'No Title';
  }

  get shouldShowTitleReadMore(): boolean {
    return this.programTitle.length > this.TITLE_LIMIT;
  }

  initChart() {
    const stats = this.data.analytics.monthlyStats;
    new Chart(this.chartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: stats.labels,
        datasets: [
          { label: 'Accepted', data: stats.accepted, backgroundColor: '#0d6efd', borderRadius: 4 },
          { label: 'Rejected', data: stats.rejected, backgroundColor: '#e2e8f0', borderRadius: 4 },
          { label: 'Pending', data: stats.pending, backgroundColor: '#ffc107', borderRadius: 4 }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: true, position: 'bottom', labels: { boxWidth: 12, font: { family: 'Inter' } } } },
        scales: {
          x: { grid: { display: false } },
          y: { beginAtZero: true, grid: { color: '#f0f0f0', ...({ borderDash: [5, 5] } as any) } }
        }
      }
    });
  }

  closeProgram(id: number): void {
    if (confirm('Are you sure you want to close this program?')) {
      this.programService.closeProgram(id).subscribe({
        next: (response) => {
          this.data.prog.status = 'CLOSED';
          this.toast.show('Program closed successfully!', 'success');
          this.cdr.detectChanges();
        },
        error: (err) => {
          const errorMsg = err.error?.message || 'An unexpected error occurred while closing the program.';
          this.toast.show(errorMsg, 'danger');
        }
      });
    }
  }

  deleteProgram(id: number) {
    if (confirm('Are you sure?')) {
      this.programService.deleteProgram(id).subscribe({
        next: () => {
          this.toast.show('Draft program deleted successfully!', 'success');
          this.programDeleted.emit(id);
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