import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReviewService } from '../service/review.service';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review-form.component.html'
})
export class ReviewFormComponent implements OnInit {
  reviewData: any;
  score: number | null = null;
  comments: string = '';

  constructor(
    // 1. CHANGED TO PUBLIC so the HTML can see it
    public router: Router, 
    private reviewService: ReviewService
  ) {
    const navigation = this.router.getCurrentNavigation();
    this.reviewData = navigation?.extras.state?.['data'];
  }

  ngOnInit(): void {
    if (!this.reviewData) {
      this.router.navigate(['/reviewer-dashboard']);
      return;
    }
    this.loadProposalLink();
  }

  loadProposalLink() {
    const pid = this.reviewData.proposalID || this.reviewData.proposalId;
    this.reviewService.getProposalById(pid).subscribe({
      next: (proposal) => {
        this.reviewData.fileURI = proposal.fileURI;
      },
      error: (err) => console.error("Could not fetch document link", err)
    });
  }

  // 2. RESTORED ARGUMENT so the HTML (click) doesn't complain
  viewDocument(url: string | undefined) {
    const finalUrl = url || this.reviewData?.fileURI;
    if (finalUrl) {
      window.open(finalUrl, '_blank');
    } else {
      alert("Document link is still loading...");
    }
  }

  submitReview() {
    const payload = {
      proposalId: this.reviewData.proposalID || this.reviewData.proposalId,
      reviewerId: this.reviewData.reviewerID || this.reviewData.reviewerId,
      score: this.score,
      comments: this.comments,
      status: 'REVIEWED',
      date: new Date().toISOString().split('T')[0]
    };

    this.reviewService.updateReview(this.reviewData.reviewID, payload).subscribe({
      next: () => {
        alert('Review Submitted successfully!');
        this.router.navigate(['/reviewer-dashboard']);
      },
      error: (err) => alert('Submission failed')
    });
  }
}