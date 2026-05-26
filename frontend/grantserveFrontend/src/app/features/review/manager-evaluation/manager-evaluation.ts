import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';
import { EvaluationService } from '../service/evaluation.service';

@Component({
  selector: 'app-manager-evaluation',
  standalone: true,
  imports: [CommonModule, ManagerHeaderComponent],
  templateUrl: './manager-evaluation.html'
})
export class ManagerEvaluationComponent implements OnInit {
  readyApplications = signal<any[]>([]);
  selectedAppId = signal<number | null>(null);
  selectedReviews = signal<any[]>([]);

  constructor(private evaluationService: EvaluationService) {}

  ngOnInit() {
    this.loadReadyApplications();
  }

  loadReadyApplications() {
    this.evaluationService.getAllApplications().subscribe({
      next: (data) => {
        this.readyApplications.set(data || []);
      },
      error: (err) => console.error("Error loading applications:", err)
    });
  }

  getSelectedAppStatus(): string {
    const appId = this.selectedAppId();
    const app = this.readyApplications().find(a => a.applicationID === appId);
    return app ? app.status : '';
  }

  selectApplication(id: number) {
    if (!id) return;
    this.selectedAppId.set(id);
    this.evaluationService.getReviewsByApplicationId(id).subscribe({
      next: (reviews) => {
        this.selectedReviews.set(reviews || []);
      },
      error: (err) => {
        console.error("Error loading reviews:", err);
        this.selectedReviews.set([]);
      }
    });
  }

  submitDecision(status: 'APPROVED' | 'REJECTED') {
    const appId = this.selectedAppId();
    if (!appId) return;

    const payload = { 
      applicationID: appId, 
      result: status, 
      date: new Date().toISOString().split('T')[0], 
      notes: `The manager has marked this application as ${status}.` 
    };

    this.evaluationService.submitEvaluation(payload).subscribe({
      next: (response) => {
        alert(`Application ${status} successfully!`);
        this.selectedAppId.set(null);
        this.loadReadyApplications(); 
      },
      error: (err) => {
        console.error("Submission failed:", err);
        alert("Submission failed. Ensure the backend is running and the payload format is correct.");
      }
    });
  }
}