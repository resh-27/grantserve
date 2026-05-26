import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EvaluationService {
  // Point this to your Application Service endpoint
  private appServiceUrl = 'http://localhost:8081/application-service/GrantApplication';
  // Point this to your Evaluation Service endpoint
  private evalServiceUrl = 'http://localhost:8081/evaluation-service/evaluation';
  // Point this to your Review Service endpoint
  private reviewServiceUrl = 'http://localhost:8081/review-service/review';

  constructor(private http: HttpClient) { }

  // 1. Fetch ALL applications from the Application Service (This fixes the missing App #2)
  getAllApplications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.appServiceUrl}/all`);
  }

  // 2. Fetch reviews for a specific application
  getReviewsByApplicationId(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.reviewServiceUrl}/application/${id}`);
  }

  // 3. Submit the final manager decision to the Evaluation Service
  submitEvaluation(data: any): Observable<string> {
    return this.http.post(`${this.evalServiceUrl}/submit`, data, { 
      responseType: 'text' 
    });
  }
}