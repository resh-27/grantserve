import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReviewService, Review } from '../service/review.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reviewer-dashboard',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './reviewer-dashboard.component.html',
  styleUrl: './reviewer-dashboard.component.css'
})
export class ReviewerDashboard implements OnInit {
  reviews = signal<Review[]>([]);
  reviewerId: number | null = null;

  constructor(
    private reviewService: ReviewService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    const storedId = localStorage.getItem('userId');
    
    if (storedId) {
      this.reviewerId = Number(storedId);
      this.loadDashboard();
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadDashboard() {
    if (this.reviewerId) {
      this.reviewService.getReviewerDashboard(this.reviewerId).subscribe({
        next: (data) => {
          this.reviews.set(data);
        },
        error: (err) => console.error("Could not load dashboard", err)
      });
    }
  }

  goToReview(selectedReview: Review) {
    this.router.navigate(['/review-form'], { 
      state: { data: selectedReview } 
    });
  }

  logout() {
    localStorage.clear(); 
    this.router.navigate(['/']);
  }
}