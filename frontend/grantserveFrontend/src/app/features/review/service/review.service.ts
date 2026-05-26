import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Review {
  reviewID: number;
  proposalId: number; // Ensure camelCase matches your component usage
  reviewerId?: number; 
  status: string;
  score: number | null;
  date?: string;
  comments?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private readonly base = environment.BASE_URL;

  constructor(private http: HttpClient) { }

  // --- REVIEW SERVICE ---
  
  getReviewerDashboard(reviewerId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.base}/review-service/review/dashboard/${reviewerId}`);
  }

  /**
   * Use this to post new assignments. 
   * It handles the plain text response from your backend.
   */
  assignReview(reviewData: any): Observable<string> {
    return this.http.post(`${this.base}/review-service/review/assign`, reviewData, { 
      responseType: 'text' 
    });
  }

  updateReview(reviewId: number, updateData: any): Observable<string> {
    return this.http.put(`${this.base}/review-service/review/update/${reviewId}`, updateData, { 
      responseType: 'text' 
    });
  }

  // --- AUTH SERVICE ---
  
  /**
   * Fetches all users with the role 'REVIEWER'.
   */
  getAllReviewers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/auth-service/auth/role/REVIEWER`);
  }

  // --- APPLICATION SERVICE ---
  
  /**
   * Fetches all grant applications for the Manager Console.
   */
  getAllApplications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/application-service/GrantApplication/all`);
  }

  /**
   * Fetches proposals associated with a specific application.
   * This is key for checking statuses like 'SUBMITTED' or 'UNDER_REVIEW'.
   */
  getProposalsByAppId(appId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/application-service/proposal/getProposal/${appId}`);
  }

  getProposalById(proposalId: number): Observable<any> {
    return this.http.get<any>(`${this.base}/application-service/proposal/${proposalId}`);
  }

  updateApplicationStatus(appId: number, status: string): Observable<boolean> {
  // Matches: @PutMapping("/updateStatusById") with @RequestParam
  return this.http.put<boolean>(
    `${this.base}/application-service/GrantApplication/updateStatusById?status=${status}&Id=${appId}`, 
    {}
  );
}
}