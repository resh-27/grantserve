export interface ApplicationCardDto {
  applicationID: number;
  researcherId: number;
  programId: number;
  title: string;
  submittedDate: string;
  status: string;
  proposals: any[];
}