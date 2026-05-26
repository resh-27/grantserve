import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  ComplianceRecord,
  ComplianceRecordDto,
  ComplianceResult,
} from '../model/compliance.model';

@Injectable({ providedIn: 'root' })
export class ComplianceService {

  private readonly base = `${environment.BASE_URL}/audit-compliance-service/GrantServe`;

  constructor(private http: HttpClient) {}

  // POST /GrantServe/createComplianceRecord
  createComplianceRecord(dto: ComplianceRecordDto): Observable<string> {
    return this.http.post(
      `${this.base}/createComplianceRecord`,
      dto,
      { responseType: 'text' }
    );
  }

  // GET /GrantServe/complianceRecords
  getAllComplianceRecords(): Observable<ComplianceRecord[]> {
    return this.http.get<ComplianceRecord[]>(`${this.base}/complianceRecords`);
  }

  // GET /GrantServe/getComplianceRecord/{id}
  getComplianceRecordById(id: number): Observable<ComplianceRecord> {
    return this.http.get<ComplianceRecord>(
      `${this.base}/getComplianceRecord/${id}`
    );
  }

  // GET /GrantServe/getComplianceRecordByResult/{result}
  getComplianceRecordByResult(
    result: ComplianceResult
  ): Observable<ComplianceRecord[]> {
    return this.http.get<ComplianceRecord[]>(
      `${this.base}/getComplianceRecordByResult/${result}`
    );
  }

  // PATCH /GrantServe/updateComplianceRecordResult/{id}
  // Backend reads full ComplianceRecord body but only updates `result`
  updateComplianceRecordResult(
    id: number,
    record: ComplianceRecord
  ): Observable<ComplianceRecord> {
    return this.http.patch<ComplianceRecord>(
      `${this.base}/updateComplianceRecordResult/${id}`,
      record
    );
  }

  // DELETE /GrantServe/DeleteComplianceRecord/{id}
  deleteComplianceRecord(id: number): Observable<string> {
    return this.http.delete(
      `${this.base}/DeleteComplianceRecord/${id}`,
      { responseType: 'text' }
    );
  }

  // PUT /GrantServe/approveDocument/{docId}
  approveDocument(docId: number): Observable<string> {
    return this.http.put(
      `${this.base}/approveDocument/${docId}`,
      {},
      { responseType: 'text' }
    );
  }
}
