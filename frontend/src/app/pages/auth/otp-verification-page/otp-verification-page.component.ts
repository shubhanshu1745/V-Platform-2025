import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { OtpVerificationRequest } from '../../../models/otp-verification-request.molule';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-otp-verification-page',
  templateUrl: './otp-verification-page.component.html',
  styleUrls: ['./otp-verification-page.component.scss'],
  imports: [ReactiveFormsModule, NgIf]
})
export class OtpVerificationPageComponent implements OnInit {
  otpForm: FormGroup;
  private authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private errorService = inject(ErrorService);
  private route = inject(ActivatedRoute);
  loading = signal<boolean>(false);
  email: string | null = null;

  constructor(private fb: FormBuilder) {
    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]] // 6-digit OTP validation
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.email = params['email']; // Get email from query parameters
      if (!this.email) {
        // Redirect to registration if email is missing (or handle as needed)
        this.router.navigate(['/register']);
      }
    });
  }

  onSubmit(): void {
    if (this.otpForm.valid && this.email) {
      this.loading.set(true);
      const otpVerificationRequest: OtpVerificationRequest = {
        email: this.email,
        otp: this.otpForm.value.otp
      };
      this.authService.verifyOtp(otpVerificationRequest).subscribe({
        next: (token) => {
          this.loading.set(false);
          // TODO: Store JWT token securely (e.g., AuthService, auth store)
          localStorage.setItem('jwtToken', token); // Example: LocalStorage (adjust as needed)
          this.notificationService.showSuccess('Email verified successfully. Logging you in...');
          this.router.navigate(['/dashboard']); // Redirect to dashboard after verification
        },
        error: (error) => {
          this.loading.set(false);
          this.errorService.handleHttpError(error, 'OTP verification failed');
        }
      });
    } else {
      // Form is invalid or email is missing
    }
  }
}