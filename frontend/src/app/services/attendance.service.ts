// src/app/services/attendance.service.ts (Up-to-date)
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Attendance } from '../models/attendance.molule'; // Corrected import to .model.ts
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = environment.apiUrl + '/events';

  constructor(private http: HttpClient) { }

  recordAttendance(eventId: number, attendanceData: Attendance): Observable<Attendance> {
    return this.http.post<Attendance>(`${this.apiUrl}/${eventId}/attendance`, attendanceData);
  }

  getAttendanceForVolunteer(eventId: number, volunteerId: number): Observable<Attendance> {
    return this.http.get<Attendance>(`${this.apiUrl}/${eventId}/attendance/${volunteerId}`);
  }

  getAllEventAttendance(eventId: number): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.apiUrl}/${eventId}/attendance`);
  }
}