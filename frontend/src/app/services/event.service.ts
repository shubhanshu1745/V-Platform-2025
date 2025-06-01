// src/app/services/event.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event } from '../models/event.molule';
import { environment } from '../../environments/environment';
import { Application } from '../models/application.module';

@Injectable({
    providedIn: 'root'
})
export class EventService {
    private apiUrl = environment.apiUrl + '/events';

    constructor(private http: HttpClient) { }

    // src/app/services/event.service.ts
createEvent(eventPayload: any): Observable<Event> {
    const createEventUrl = `${this.apiUrl}/create`;
    return this.http.post<Event>(createEventUrl, eventPayload); // Angular will set Content-Type to application/json
  }
  
    getEvent(eventId: number): Observable<Event> {
        return this.http.get<Event>(`${this.apiUrl}/${eventId}`);
    }

    getAllEvents(pincode?: string, startDate?: string, endDate?: string, skills?: string, query?: string): Observable<Event[]> {
        let params = new HttpParams();
        if (pincode) params = params.set('pincode', pincode);
        if (startDate) params = params.set('startDate', startDate);
        if (endDate) params = params.set('endDate', endDate);
        if (skills) params = params.set('skills', skills);
        if (query) params = params.set('query', query);

        return this.http.get<Event[]>(`${this.apiUrl}`, { params });
    }

    applyForEvent(eventId: number): Observable<Application> {
        return this.http.post<Application>(`${this.apiUrl}/${eventId}/apply`, {});
    }

    getApplicants(eventId: number): Observable<Application[]> {
        return this.http.get<Application[]>(`${this.apiUrl}/${eventId}/applicants`);
    }

    approveApplication(applicationId: number): Observable<Application> {
        return this.http.put<Application>(`/api/v1/applications/${applicationId}/approve`, {});
    }

    rejectApplication(applicationId: number): Observable<Application> {
        return this.http.put<Application>(`/api/v1/applications/${applicationId}/reject`, {});
    }

    getAppliedEventsForVolunteer(): Observable<Event[]> {
        return this.http.get<Event[]>(`${this.apiUrl}/applied`);
    }

    rejectApplicationForEventApplicants(eventId: number, applicationId: number): Observable<Application> {
        return this.http.put<Application>(`${this.apiUrl}/${eventId}/applicants/${applicationId}/reject`, {});
    }

    approveApplicationForEventApplicants(eventId: number, applicationId: number): Observable<Application> {
        return this.http.put<Application>(`${this.apiUrl}/${eventId}/applicants/${applicationId}/approve`, {});
    }
}