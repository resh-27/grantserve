import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../service/review.service';
import { ManagerHeaderComponent } from '../../../shared/components/navigation/manager-header.component/manager-header.component';

@Component({
  selector: 'app-manager-assignment',
  standalone: true,
  imports: [CommonModule, FormsModule, ManagerHeaderComponent],
  templateUrl: './manager-assignment.html',
  styleUrls: ['./manager-assignment.css']
})
export class ManagerAssignmentComponent implements OnInit {
  applications = signal<any[]>([]);
  proposals = signal<any[]>([]);
  reviewers = signal<any[]>([]);

  selectedApplicationId: number | null = null;
  selectedProposalId: number | null = null;
  selectedReviewerId: number | null = null;

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.fetchInitialData();
  }

  fetchInitialData(): void {
    this.reviewService.getAllApplications().subscribe({
      next: (data: any[]) => this.applications.set(data || []),
      error: (err: any) => console.error("Error fetching apps:", err)
    });

    this.reviewService.getAllReviewers().subscribe({
      next: (data: any[]) => this.reviewers.set(data || []),
      error: (err: any) => console.error("Error fetching reviewers:", err)
    });
  }

  selectApplication(id: number): void {
    this.selectedApplicationId = id;
    this.selectedProposalId = null;
    this.selectedReviewerId = null;
    this.proposals.set([]);

    this.reviewService.getProposalsByAppId(id).subscribe({
      next: (data: any[]) => this.proposals.set(data || []),
      error: (err: any) => console.error("Failed to load proposals", err)
    });
  }

 
  hasPendingProposals(): boolean {
    const currentApp = this.applications().find(a => a.applicationID === this.selectedApplicationId);
    // If application is not approved, we allow more assignments to new reviewers
    return currentApp?.status !== 'APPROVED';
  }

  // manager-assignment.component.ts

submitAssignment(): void {
  if (!this.selectedProposalId || !this.selectedReviewerId || !this.selectedApplicationId) return;

  const payload = {
    proposalId: Number(this.selectedProposalId),
    reviewerId: Number(this.selectedReviewerId),
    score: 0,
    comments: "Assigned by Manager.",
    date: new Date().toISOString().split('T')[0],
    status: 'UNDER_REVIEW'
  };

  // Step 1: Assign the Reviewer
  this.reviewService.assignReview(payload).subscribe({
    next: (response: string) => {
      if (response.toLowerCase().includes("already assigned")) {
        alert("This specific reviewer is already assigned to this proposal.");
      } else {
        
        // Step 2: Update Application Status to 'UNDER_REVIEW'
        this.reviewService.updateApplicationStatus(Number(this.selectedApplicationId), 'UNDER_REVIEW').subscribe({
          next: (isUpdated: boolean) => {
            if (isUpdated) {
              console.log("Database status updated to UNDER_REVIEW");
            }
          },
          error: (err: any) => console.error("Status update failed", err)
        });

        alert('Proposal assigned successfully!');
        
        // Step 3: Refresh UI data
        this.fetchInitialData(); 
        this.reviewService.getProposalsByAppId(Number(this.selectedApplicationId)).subscribe({
          next: (data: any[]) => this.proposals.set(data || []),
          error: (err: any) => console.error(err)
        });

        this.selectedReviewerId = null; 
      }
    },
    error: (err: any) => alert('Error: Reviewer might already be assigned.')
  });
}
}