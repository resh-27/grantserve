import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Audit, AuditDto, AuditStatus } from '../model/audit.model';

@Injectable({ providedIn: 'root' })
export class AuditService {

  private readonly base = `${environment.BASE_URL}/audit-compliance-service/GrantServe`;

  constructor(private http: HttpClient) {}

  // POST /GrantServe/createAudit
  createAudit(dto: AuditDto): Observable<string> {
    return this.http.post(`${this.base}/createAudit`, dto, { responseType: 'text' });
  }

  // GET /GrantServe/audits
  getAllAudits(): Observable<Audit[]> {
    return this.http.get<Audit[]>(`${this.base}/audits`);
  }

  // GET /GrantServe/getAudit/{id}
  getAuditById(id: number): Observable<Audit> {
    return this.http.get<Audit>(`${this.base}/getAudit/${id}`);
  }

  // GET /GrantServe/getAuditByStatus/{status}
  getAuditByStatus(status: AuditStatus): Observable<Audit[]> {
    return this.http.get<Audit[]>(`${this.base}/getAuditByStatus/${status}`);
  }

  // PATCH /GrantServe/updateAuditStatus/{id}
  // Backend reads the full Audit body but only updates `status`
  updateAuditStatus(id: number, audit: Audit): Observable<Audit> {
    return this.http.patch<Audit>(
      `${this.base}/updateAuditStatus/${id}`,
      audit
    );
  }

  // DELETE /GrantServe/deleteAudit/{id}
  deleteAudit(id: number): Observable<string> {
    return this.http.delete(`${this.base}/deleteAudit/${id}`, { responseType: 'text' });
  }
}
