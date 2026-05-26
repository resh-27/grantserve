import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../service/audit.service';
import { Audit, AuditDto, AuditScope, AuditStatus } from '../model/audit.model';

type View = 'list' | 'detail' | 'create';

@Component({
  selector: 'app-audit-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-dashboard.component.html',
  styleUrl: './audit-dashboard.component.css',
})
export class AuditDashboardComponent implements OnInit {

  // ── view state ─────────────────────────────────────────────────────────────
  currentView: View = 'list';
  selectedAudit: Audit | null = null;

  // ── data ───────────────────────────────────────────────────────────────────
  audits: Audit[] = [];
  filteredAudits: Audit[] = [];
  searchTerm = '';
  filterStatus: AuditStatus | '' = '';
  isLoading = false;
  errorMsg = '';
  successMsg = '';

  // ── create form ────────────────────────────────────────────────────────────
  newAudit: AuditDto = { officerID: 0, scope: 'APPLICATION', findings: '' };
  createError = '';

  // ── status update (detail view) ────────────────────────────────────────────
  updateStatus: AuditStatus = 'PENDING';

  // ── enum options for templates ─────────────────────────────────────────────
  readonly scopeOptions: AuditScope[]   = ['APPLICATION', 'PROGRAM'];
  readonly statusOptions: AuditStatus[] = ['COMPLETED', 'PENDING', 'ISSUE'];

  constructor(private auditService: AuditService) {}

  ngOnInit(): void { this.loadAudits(); }

  // ── LOAD ALL ───────────────────────────────────────────────────────────────
  loadAudits(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.auditService.getAllAudits().subscribe({
      next: (data) => {
        this.audits = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Failed to load audits.';
        this.isLoading = false;
      },
    });
  }

  // ── FILTER / SEARCH ────────────────────────────────────────────────────────
  applyFilter(): void {
    let list = [...this.audits];
    if (this.filterStatus) {
      list = list.filter((a) => a.status === this.filterStatus);
    }
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase();
      list = list.filter(
        (a) =>
          a.auditID.toString().includes(q) ||
          a.officerID.toString().includes(q) ||
          a.scope.toLowerCase().includes(q) ||
          a.findings.toLowerCase().includes(q)
      );
    }
    this.filteredAudits = list;
  }

  onSearchChange(): void { this.applyFilter(); }
  onFilterChange(): void { this.applyFilter(); }

  // ── NAVIGATE VIEWS ─────────────────────────────────────────────────────────
  openDetail(audit: Audit): void {
    this.selectedAudit = audit;
    this.updateStatus = audit.status;
    this.successMsg = '';
    this.errorMsg = '';
    this.currentView = 'detail';
  }

  openCreate(): void {
    this.newAudit = { officerID: 0, scope: 'APPLICATION', findings: '' };
    this.createError = '';
    this.currentView = 'create';
  }

  backToList(): void {
    this.successMsg = '';
    this.errorMsg = '';
    this.currentView = 'list';
    this.loadAudits();
  }

  // ── CREATE ─────────────────────────────────────────────────────────────────
  submitCreate(): void {
    if (!this.newAudit.officerID || !this.newAudit.findings.trim()) {
      this.createError = 'Officer ID and findings are required.';
      return;
    }
    this.createError = '';
    this.auditService.createAudit(this.newAudit).subscribe({
      next: (msg) => {
        this.successMsg = msg || 'Audit created successfully!';
        this.backToList();
      },
      error: (err) => {
        this.createError = err.error?.message || 'Failed to create audit.';
      },
    });
  }

  // ── UPDATE STATUS ──────────────────────────────────────────────────────────
  submitStatusUpdate(): void {
    if (!this.selectedAudit) return;
    
    // Create a payload with the full object but updated status
    const payload = { ...this.selectedAudit, status: this.updateStatus };
    
    this.auditService
      .updateAuditStatus(this.selectedAudit.auditID, payload)
      .subscribe({
        next: (updated) => {
          this.selectedAudit = updated;
          this.successMsg = `Audit #${updated.auditID} status updated to ${updated.status}.`;
          
          // Update the list locally
          const index = this.audits.findIndex(a => a.auditID === updated.auditID);
          if (index !== -1) {
            this.audits[index] = updated;
            this.applyFilter();
          }
        },
        error: (err) => {
          this.errorMsg = err.error?.message || 'Status update failed.';
        },
      });
  }

  // ── QUICK STATUS BUTTONS (detail view) ────────────────────────────────────
  markStatus(status: AuditStatus): void {
    this.updateStatus = status;
    this.submitStatusUpdate();
  }

  // ── DELETE ─────────────────────────────────────────────────────────────────
  deleteAudit(id: number): void {
    if (!confirm(`Delete Audit #${id}? This cannot be undone.`)) return;
    this.auditService.deleteAudit(id).subscribe({
      next: (msg) => {
        this.successMsg = msg || `Audit #${id} deleted.`;
        this.backToList();
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Delete failed.';
      },
    });
  }

  // ── HELPERS ────────────────────────────────────────────────────────────────
  statusClass(status: AuditStatus): string {
    return {
      COMPLETED: 'pill pill--success',
      PENDING:   'pill pill--warning',
      ISSUE:     'pill pill--danger',
    }[status] ?? 'pill';
  }
}
