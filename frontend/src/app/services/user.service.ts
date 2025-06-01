// src/app/services/user.service.ts (Up-to-date)
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.molule';// Corrected import to .model.ts
import { ProfileUpdateRequest } from '../models/profile-update-request.molule';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.apiUrl + '/users';

  constructor(private http: HttpClient) { }

  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me/profile`);
  }
  getUserProfile(volunteerId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile/${volunteerId}`);
  }

  updateUserProfile(userEmail: string, updateRequestDTO: ProfileUpdateRequest): Observable<User> {
    const formData = new FormData();

    const dtoBlob = new Blob([JSON.stringify(updateRequestDTO)], { type: 'application/json' });
    formData.append('profileUpdateRequestDTO', dtoBlob);

    if (updateRequestDTO.photoFile) {
      formData.append('photoFile', updateRequestDTO.photoFile, updateRequestDTO.photoFile.name);
    }

    return this.http.put<User>(`${this.apiUrl}/profile`, formData);
  }

  searchUsers(pincode?: string, query?: string): Observable<User[]> {
    let params = new HttpParams();
    if (pincode) params = params.set('pincode', pincode);
    if (query) params = params.set('query', query);

    return this.http.get<User[]>(`${this.apiUrl}/search`, { params });
  }

  searchVolunteersForOrganizer(skills?: string, pincode?: string): Observable<User[]> { // Added for organizer search
    let params = new HttpParams();
    if (skills) params = params.set('skills', skills);
    if (pincode) params = params.set('pincode', pincode);

    return this.http.get<User[]>(`${this.apiUrl}/organizer-search`, { params }); // Corrected endpoint URL
  }
}