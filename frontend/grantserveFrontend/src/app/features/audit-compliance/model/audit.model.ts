// ─── Enums matching backend ────────────────────────────────────────────────

export type AuditScope = 'APPLICATION' | 'PROGRAM';
export type AuditStatus = 'COMPLETED' | 'PENDING' | 'ISSUE';

// ─── Audit Entity (returned from backend) ───────────────────────────────────

export interface Audit {
  auditID: number;
  officerID: number;
  scope: AuditScope;
  findings: string;
  date: string;         // LocalDate serialised as ISO string
  status: AuditStatus;
}

// ─── Audit DTO (sent to backend POST /createAudit) ──────────────────────────

export interface AuditDto {
  auditID?: number;
  officerID: number;
  scope: AuditScope;
  findings: string;
}

// ─── Status-update payload (PATCH /updateAuditStatus/{id}) ──────────────────
// Backend expects a full Audit body but only uses `status`

export interface AuditStatusUpdate {
  status: AuditStatus;
}
