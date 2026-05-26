import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplianceService } from '../service/compliance.service';
import { ComplianceRecord, ComplianceRecordDto, ComplianceResult, ComplianceType } from '../model/compliance.model';
import { ResearcherService } from '../../researcher/service/researcher.service';
import { ResearcherDocument } from '../../researcher/model/researcher.model';

type View = 'list' | 'detail' | 'create';

@Component({
  selector: 'app-compliance-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './compliance-dashboard.component.html',
  styleUrl: './compliance-dashboard.component.css',
})
export class ComplianceDashboardComponent implements OnInit {
  currentView: View = 'list';
  selectedRecord: ComplianceRecord | null = null;
  records: ComplianceRecord[] = [];
  filteredRecords: ComplianceRecord[] = [];
  unapprovedDocuments: ResearcherDocument[] = [];
  pendingDocs: ResearcherDocument[] = [];
  
  searchTerm = '';
  filterResult: ComplianceResult | '' = '';
  isLoading = false;
  isLoadingDocs = false;
  errorMsg = '';
  successMsg = '';

  // FIX: Added missing variables for Create View
  createError = ''; 
  newRecord: ComplianceRecordDto = { entityID: 0, type: 'APPLICATION', notes: '' };
  
  updateResult: ComplianceResult = 'PENDING';

  readonly typeOptions: ComplianceType[] = ['APPLICATION', 'PROGRAM'];
  readonly resultOptions: ComplianceResult[] = ['COMPLIANT', 'VIOLATION', 'PENDING'];

  constructor(
    public complianceService: ComplianceService, 
    public researcherService: ResearcherService   
  ) {}

  ngOnInit(): void { 
    this.loadRecords(); 
    this.loadUnapprovedDocuments();
  }

  loadRecords(): void {
    this.isLoading = true;
    this.complianceService.getAllComplianceRecords().subscribe({
      next: (data: ComplianceRecord[]) => {
        this.records = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err: any) => {
        this.errorMsg = err.error?.message || 'Failed to load records.';
        this.isLoading = false;
      }
    });
  }

  loadUnapprovedDocuments(): void {
    this.isLoadingDocs = true;
    this.researcherService.getPendingQueue().subscribe({
      next: (data: ResearcherDocument[]) => {
        this.unapprovedDocuments = data;
        this.pendingDocs = data;
        this.isLoadingDocs = false;
      },
      error: (err: any) => {
        console.error("Could not load queue", err);
        this.isLoadingDocs = false;
      }
    });
  }

  // FIX: Added missing submitCreate method
  submitCreate(): void {
    if (!this.newRecord.entityID) {
      this.createError = 'Entity ID is required.';
      return;
    }
    this.createError = '';
    this.complianceService.createComplianceRecord(this.newRecord).subscribe({
      next: (msg: string) => {
        this.successMsg = msg || 'Record created successfully!';
        this.backToList();
      },
      error: (err: any) => {
        this.createError = err.error?.message || 'Failed to create record.';
      }
    });
  }

  approveDocument(id: number): void {
    this.researcherService.updateVerificationStatus(id, 'Approved').subscribe({
      next: () => {
        alert("Document Approved!");
        this.loadUnapprovedDocuments();
      },
      error: (err: any) => console.error(err)
    });
  }

  rejectDoc(id: number): void {
    this.researcherService.updateVerificationStatus(id, 'Rejected').subscribe({
      next: () => {
        alert("Document Rejected");
        this.loadUnapprovedDocuments();
      },
      error: (err: any) => console.error(err)
    });
  }

  applyFilter(): void {
    let list = [...this.records];
    if (this.filterResult) list = list.filter((r) => r.result === this.filterResult);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase();
      list = list.filter(r => 
        r.complianceID.toString().includes(q) || 
        r.entityID.toString().includes(q) ||
        (r.notes ?? '').toLowerCase().includes(q)
      );
    }
    this.filteredRecords = list;
  }
  
  onSearchChange(): void { this.applyFilter(); }
  onFilterChange(): void { this.applyFilter(); }
  openDetail(record: ComplianceRecord): void { 
    this.selectedRecord = record; 
    this.updateResult = record.result;
    this.currentView = 'detail'; 
  }
  openCreate(): void { 
    this.createError = '';
    this.newRecord = { entityID: 0, type: 'APPLICATION', notes: '' };
    this.currentView = 'create'; 
  }
  backToList(): void { this.currentView = 'list'; this.loadRecords(); }
  
  resultClass(result: ComplianceResult): string {
    const classes: any = { COMPLIANT: 'pill pill--success', PENDING: 'pill pill--warning', VIOLATION: 'pill pill--danger' };
    return classes[result] || 'pill';
  }

  submitResultUpdate(): void {
    if (!this.selectedRecord) return;
    const payload = { ...this.selectedRecord, result: this.updateResult };
    this.complianceService.updateComplianceRecordResult(this.selectedRecord.complianceID, payload).subscribe({
      next: (updated: ComplianceRecord) => {
        this.successMsg = "Updated successfully";
        this.loadRecords();
      }
    });
  }
}