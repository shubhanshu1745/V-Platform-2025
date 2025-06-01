import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { AuthRequest } from '../models/auth-request.molule';
import { OtpVerificationRequest } from '../models/otp-verification-request.molule';
import { User } from '../models/user.molule';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { RoleType } from '../models/role-type.enum.enum';
import { ErrorService } from './error.service';

interface LoginResponse { // Interface for Login Response (though backend sends just token string)
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  // Use Angular signals for auth state
  isAuthenticated = signal<boolean>(false);
  currentRole = signal<RoleType>(RoleType.VOLUNTEER);
  // Store token locally in the service
  private token: string | null = null;

  constructor(private http: HttpClient, private router: Router, private errorService: ErrorService) {
    // No automatic localStorage checks in constructor anymore.
    
  }

  // Call this from a component to update the auth state
  checkAuthStatus(): void {
    console.log('AuthService.checkAuthStatus() - Called');
    const storedToken = this.token || localStorage.getItem('jwtToken');
    console.log('AuthService.checkAuthStatus() - Retrieved token:', storedToken);
    this.isAuthenticated.set(!!storedToken);
  }

  register(authRequest: AuthRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, authRequest);
  }

  verifyOtp(otpVerificationRequest: OtpVerificationRequest): Observable<string> {
    return this.http.post<string>(
      `${this.apiUrl}/verify-otp`,
      otpVerificationRequest,
      { responseType: 'text' as 'json' }
    );
  }

  login(authRequest: AuthRequest): Observable<LoginResponse> {
    return this.http
      .post(`${this.apiUrl}/login`, authRequest, { responseType: 'text' as 'json' })
      .pipe(
        tap((response: any) => {
          const token = response as string;
          const loginResponse: LoginResponse = { token };
          localStorage.setItem('jwtToken', loginResponse.token);
          this.token = loginResponse.token;
          console.log('AuthService.login() - Token stored:', localStorage.getItem('jwtToken'));
          this.isAuthenticated.set(true);
        }),
        map((response: any) => ({ token: response as string })),
        catchError((error) => { // Add catchError operator
          this.errorService.handleHttpError(error, 'Login failed. Please check your credentials.'); // Handle error using ErrorService
          return throwError(() => error); // Re-throw the error so the component knows login failed
        })
      );
  }
  

  logout(): void {
    localStorage.removeItem('jwtToken');
    this.token = null;
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  switchRole(role: string): Observable<any> {
    console.log('AuthService.switchRole() - Started, role:', role);
    const token = this.getToken();
    console.log('AuthService.switchRole() - Retrieved token:', token);
    if (!token) {
      console.warn("No JWT token found. Cannot switch roles.");
      return new Observable<any>(); // or throw an error if desired
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` // note the correct template literal usage
    });
    return this.http.post<any>(
      `${this.apiUrl}/switch-role`,
      { role },
      { headers }
    ).pipe(
      tap((response: any) => {
        console.log('AuthService.switchRole() - Response:', response);
        // Update stored token if backend returns a new one
        localStorage.setItem('jwtToken', response.token);
        this.token = response.token;
        this.currentRole.set(role as RoleType);
      })
    );
  }

  getToken(): string | null { // Simplified getToken - directly from localStorage
    console.log('AuthService.getToken() - Called');
    const token = localStorage.getItem('jwtToken'); // Directly get from localStorage
    console.log('AuthService.getToken() - Retrieved token:', token);
    return token;
  }

  getRole(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken = JSON.parse(atob(token.split('.')[1]));
        return decodedToken.roles;
      } catch (error) {
        console.error("Error decoding token to get role:", error);
        return null;
      }
    }
    return null;
  }

  getEmail(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedPayload = JSON.parse(atob(token.split('.')[1]));
        return decodedPayload?.sub || null;
      } catch (error) {
        console.error("Error decoding token for email:", error);
        return null;
      }
    }
    return null;
  }

  isAuthenticatedUser(): boolean {
    return this.isAuthenticated();
  }

  isOrganizerRole(): boolean {
    return this.currentRole() === RoleType.ORGANIZER;
  }
}