// ─── Enums matching backend ────────────────────────────────────────────────

export type ComplianceType   = 'APPLICATION' | 'PROGRAM';
export type ComplianceResult = 'COMPLIANT' | 'VIOLATION' | 'PENDING';

// ─── Compliance Record Entity (returned from backend) ───────────────────────

export interface ComplianceRecord {
  complianceID: number;
  entityID: number;      // ApplicationID or ProgramID
  type: ComplianceType;
  result: ComplianceResult;
  date: string;          // LocalDate serialised as ISO string
  notes: string;
}

// ─── Compliance DTO (sent to backend POST /createComplianceRecord) ───────────

export interface ComplianceRecordDto {
  complianceID?: number;
  entityID: number;
  type: ComplianceType;
  notes?: string;
}

// ─── Result-update payload (PATCH /updateComplianceRecordResult/{id}) ────────
// Backend expects full ComplianceRecord body but only uses `result`

export interface ComplianceResultUpdate {
  result: ComplianceResult;
}
