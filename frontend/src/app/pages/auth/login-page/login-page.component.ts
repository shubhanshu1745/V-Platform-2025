import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { AuthRequest } from '../../../models/auth-request.molule';
import { NgIf, CommonModule } from '@angular/common';
import { LoginResponse } from '../../../models/login-response.molule';// Import LoginResponse interface

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
  imports: [NgIf, ReactiveFormsModule, CommonModule], // Added CommonModule to imports
  standalone: true
})
export class LoginPageComponent {
  loginForm: FormGroup;
  private authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private errorService = inject(ErrorService);
  loading = signal<boolean>(false);

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading.set(true);
      const authRequest: AuthRequest = this.loginForm.value;
      this.authService.login(authRequest).subscribe({
        next: (response: LoginResponse) => {
          this.loading.set(false);
          // Now response.token is valid
          localStorage.setItem('jwtToken', response.token);
          this.notificationService.showSuccess('Login successful');
          this.router.navigate(['/dashboard']);
          console.log("Login successful");
        },
        
        error: (error) => {
          this.loading.set(false);
          this.errorService.handleHttpError(error, 'Login failed');
        }
      });
    } else {
      // Form is invalid
    }
  }
}