// location.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Country } from '../models/country.module';
import { State } from '../models/state.module';
import { District } from '../models/district.module';
import { Pincode } from '../models/pincode.module';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private apiUrl = environment.apiUrl + '/locations'; // e.g., 'http://localhost:8080/api/v1/locations'

  constructor(private http: HttpClient) { }

  getCountries(): Observable<Country[]> {
    return this.http.get<Country[]>(`${this.apiUrl}/countries`);
  }

  getStatesByCountry(countryId: number): Observable<State[]> {
    return this.http.get<State[]>(`${this.apiUrl}/countries/${countryId}/states`);
  }

  getDistrictsByState(stateId: number): Observable<District[]> {
    return this.http.get<District[]>(`${this.apiUrl}/states/${stateId}/districts`);
  }

  getPincodesByDistrict(districtId: number): Observable<Pincode[]> {
    return this.http.get<Pincode[]>(`${this.apiUrl}/districts/${districtId}/pincodes`);
  }

  getPincodeDetails(pincodeValue: string): Observable<Pincode> {
    return this.http.get<Pincode>(`${this.apiUrl}/pincodes/${pincodeValue}`);
  }
}