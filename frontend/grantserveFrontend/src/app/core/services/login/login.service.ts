import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponseDto } from '../../../features/Auth/model/login.model';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private authUrl = `${environment.BASE_URL}/auth-service/auth/login`;

  constructor(private http: HttpClient, private router: Router) {}
  
  login(credentials: any): Observable<UserResponseDto> {
    return this.http.post<UserResponseDto>(this.authUrl, credentials);
  }
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  redirectBasedOnRole(role: string) {
    switch (role) {
      case 'MANAGER':
        this.router.navigate(['/manager/programs']);
        break;
      case 'RESEARCHER':
        this.router.navigate(['/home']);
        break;
      case 'REVIEWER':
        this.router.navigate(['/reviewer-dashboard']);
        break;
      case 'COMPLIANCE':
      case 'AUDITOR':
        this.router.navigate(['/reports']);
        break;
      default:
        this.router.navigate(['/']);
        break;
    }
  }
}

