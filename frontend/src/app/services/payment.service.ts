// src/app/services/payment.service.ts (Up-to-date)
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment } from '../models/payment.molule'; // Corrected import to .model.ts
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = environment.apiUrl + '/payments';

  constructor(private http: HttpClient) { }

  getPaymentsForEvent(eventId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/event/${eventId}`);
  }

  getPaymentsForVolunteer(volunteerId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/volunteer/${volunteerId}`);
  }

  processPayment(eventId: number, volunteerId: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/process/${eventId}/volunteer/${volunteerId}`, {});
  }
}