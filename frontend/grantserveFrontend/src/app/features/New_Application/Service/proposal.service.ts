import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { ApplicationFormModel } from '../Model/ProposalForm';

@Injectable({
  providedIn: 'root',
})
export class ProposalService {
  constructor(private Http: HttpClient) {}

  SubmitDocument(Data: ApplicationFormModel) {
    const url = `${environment.BASE_URL}/application-service/proposal/createProposal`;
    return this.Http.post(url, Data);
  }

}
