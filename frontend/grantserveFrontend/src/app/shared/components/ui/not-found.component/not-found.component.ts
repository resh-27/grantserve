import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { JwtService } from '../../../../core/services/jwtService/jwt-service';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="d-flex align-items-center justify-content-center vh-100 bg-light">
      <div class="text-center">
        <h1 class="display-1 fw-bold text-primary">404</h1>
        <p class="fs-3"><span class="text-danger">Oops!</span> Page not found.</p>
        <p class="lead">The page you’re looking for doesn’t exist or has been moved.</p>
        <a
          [routerLink]="viewMode === 'manager' ? ['/manager/programs'] : ['/home']"
          class="btn btn-primary shadow-sm"
        >
          <i class="bi bi-house-door-fill me-2"></i>Back to Dashboard
        </a>
      </div>
    </div>
  `,
  styles: [
    `
      .display-1 {
        font-size: 8rem;
      }
    `,
  ],
})
export class NotFoundComponent {
  viewMode: 'manager' | 'researcher';

  constructor(
    private jwtService: JwtService,
    private router: Router,
  ) {
    const role = this.jwtService.getUserRole();
    this.viewMode = role === 'MANAGER' ? 'manager' : 'researcher';

    if (role === null) {
      localStorage.removeItem('token');
      this.router.navigate(['/']);
    }
  }
}
