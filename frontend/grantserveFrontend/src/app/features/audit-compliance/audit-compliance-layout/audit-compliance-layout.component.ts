import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuditDashboardComponent } from '../audit-dashboard/audit-dashboard.component';
import { ComplianceDashboardComponent } from '../compliance-dashboard/compliance-dashboard.component';

type ActiveTab = 'compliance' | 'audit';
type Role = 'AUDITOR' | 'COMPLIANCE' | string;

@Component({
  selector: 'app-audit-compliance-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    AuditDashboardComponent,
    ComplianceDashboardComponent,
  ],
  templateUrl: './audit-compliance-layout.component.html',
  styleUrl: './audit-compliance-layout.component.css',
})
export class AuditComplianceLayoutComponent implements OnInit {

  userRole: Role = '';
  userName = '';
  activeTab: ActiveTab = 'compliance';

  // Which tabs this role can access
  canSeeCompliance = false;
  canSeeAudit      = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.userRole = localStorage.getItem('userRole') ?? '';
    this.userName = localStorage.getItem('userId') ?? 'User';

    // Role-based access
    this.canSeeCompliance = this.userRole === 'COMPLIANCE';
    this.canSeeAudit      = this.userRole === 'AUDITOR';

    // Default tab based on role
    if (this.canSeeAudit && !this.canSeeCompliance) {
      this.activeTab = 'audit';
    } else {
      this.activeTab = 'compliance';
    }

    // Redirect if user has no access at all
    if (!this.canSeeAudit && !this.canSeeCompliance) {
      this.router.navigate(['/home']);
    }
  }

  switchTab(tab: ActiveTab): void {
    this.activeTab = tab;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/']);
  }

  get pageTitle(): string {
    return this.activeTab === 'compliance'
      ? 'Compliance Overview'
      : 'Audit Overview';
  }

  get roleBadgeLabel(): string {
    switch (this.userRole) {
      case 'AUDITOR':    return 'Auditor';
      case 'COMPLIANCE': return 'Compliance Officer';
      case 'ADMIN':      return 'Admin';
      default:           return this.userRole;
    }
  }
}
