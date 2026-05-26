import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtService } from '../../../core/services/jwtService/jwt-service';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import { ApplicationCard } from '../application-card/application-card';

@Injectable({
  providedIn: 'root',
})
export class ApplicationsService {
  readonly baseUrl = `${environment.BASE_URL}/application-service/GrantApplication`;

  constructor(private Http: HttpClient, private JwtService: JwtService) {}

  private get userId(): number | null {
    return this.JwtService.getUserId();
  }
// applications.service.ts
getApplications(page: number, size: number, status: string, searchTerm : string): Observable<any> {
  // We convert 'Under Review' to 'UNDER_REVIEW' for the Backend
  const formattedStatus = status.toUpperCase().replace(' ', '_');
  
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('status', formattedStatus) // Backend uses this in the WHERE clause
    .set('search', searchTerm);

  return this.Http.get<any>(`${this.baseUrl}/FetchGrantApplication/${this.userId}`, { params });
}
  searchApplications(searchTerm: string) {
  let params = new HttpParams();

  // Logic: If the input is only numbers, search by ID. Otherwise, search by Title.
  if (/^\d+$/.test(searchTerm)) {
    params = params.set('id', searchTerm);
  } else {
    params = params.set('title', searchTerm);
  }

  return this.Http.get(`${environment.BASE_URL}/application-service/GrantApplication/search/${this.userId}`, { params });
}

fetchApplications(programID: number) : Observable<ApplicationCard[]> {
  return this.Http.get<ApplicationCard[]>(`${environment.BASE_URL}/application-service/GrantApplication/ProgramGrantApplications/${programID}`);
}

fetchAppliedProgramIds(userId: number) : Observable<number[]> {
  return this.Http.get<number[]>(`${environment.BASE_URL}/application-service/GrantApplication/AppliedProgramIds/${userId}`);
}

// applications.service.ts

getUserApplicationCounts(): Observable<Record<string, number>> {
  // Ensure userId is available; if it's dynamic, pass it as a parameter
  return this.Http.get<Record<string, number>>(
    `${environment.BASE_URL}/application-service/GrantApplication/UserApplicationCount/${this.userId}`
  );
}
}
