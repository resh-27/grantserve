import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { ApplicationFormModel } from '../Model/Application_Form';

@Injectable({
  providedIn: 'root',
})
export class ApplicationService {
  constructor(private Http : HttpClient){}

  url = `${environment.BASE_URL}/application-service/GrantApplication/createApplication`;

  submitApplication(Data : ApplicationFormModel){
    return this.Http.post(this.url, Data )
  }



}
