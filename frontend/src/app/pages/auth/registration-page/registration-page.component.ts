import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { AuthRequest } from '../../../models/auth-request.molule';
import { Router, RouterModule } from '@angular/router';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-registration-page',
  templateUrl: './registration-page.component.html',
  styleUrls: ['./registration-page.component.scss'],
  imports: [RouterModule,NgIf, ReactiveFormsModule],
})
export class RegistrationPageComponent {
  registrationForm: FormGroup;
  private authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private errorService = inject(ErrorService);
  loading = signal<boolean>(false);

  constructor(private fb: FormBuilder) {
    this.registrationForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      pincode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]] // Basic pincode validation
    }, { validators: this.passwordMatchValidator }); // Custom validator for password match
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.controls['password'].value;
    const confirmPassword = group.controls['confirmPassword'].value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.registrationForm.valid) {
      this.loading.set(true);
      const authRequest: AuthRequest = this.registrationForm.value;
      this.authService.register(authRequest).subscribe({
        next: (user) => {
          this.loading.set(false);
          this.notificationService.showSuccess('Registration successful. Please verify your email.');
          this.router.navigate(['/verify-otp'], { queryParams: { email: user.email } }); // Navigate to OTP verification
        },
        error: (error) => {
          this.loading.set(false);
          this.errorService.handleHttpError(error, 'Registration failed');
        }
      });
    } else {
      // Form is invalid, display errors (Angular handles form validation display automatically)
    }
  }
}