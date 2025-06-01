// src/app/services/application.service.ts (Up-to-date)
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from '../models/application.module';  
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = environment.apiUrl + '/applications';

  constructor(private http: HttpClient) { }

  approveApplication(applicationId: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/${applicationId}/approve`, {});
  }

  rejectApplication(applicationId: number): Observable<Application> {
    return this.http.put<Application>(`${this.apiUrl}/${applicationId}/reject`, {});
  }
}