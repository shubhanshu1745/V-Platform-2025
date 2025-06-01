// Corrected edit-profile-page.component.ts (Component - Correct Variable Name - Again!)
import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';
import { ProfileUpdateRequest } from '../../../models/profile-update-request.molule';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../../components/loading-spinner/loading-spinner.component';
import { FormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-edit-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent, FormsModule, MatDatepickerModule, MatInputModule],
  templateUrl: './edit-profile-page.component.html',
  styleUrl: './edit-profile-page.component.scss'
})
export class EditProfilePageComponent implements OnInit {
  profileForm: FormGroup;
  loading = signal<boolean>(false);
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private errorService = inject(ErrorService);
  selectedFile: File | null = null;
  authService = inject(AuthService);

  constructor() {
    this.profileForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      pincode: [''],
      contactDetails: [''],
      skills: [''],
      gender: [''],
      photoFile: [null],
      availabilityCalendar: [null]
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.loading.set(true);
    this.userService.getMyProfile().subscribe({
      next: (user) => {
        this.loading.set(false);
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          pincode: user.pincode,
          contactDetails: user.contactDetails,
          skills: user.skills,
          gender: user.gender,
          availabilityCalendar: user.availabilityCalendar
        });
      },
      error: (error) => {
        this.loading.set(false);
        this.errorService.handleHttpError(error, 'Failed to load user profile for editing');
      }
    });
  }
  onFileChange(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      this.loading.set(true);
      const updateRequest: ProfileUpdateRequest = this.profileForm.value; // <--- updateRequest is correctly defined here
      updateRequest.photoFile = this.selectedFile;

      // --- Availability Calendar Feature: Get Date Value ---
      const availabilityDate = this.profileForm.get('availabilityCalendar')?.value;
      if (availabilityDate) {
        updateRequest.availabilityCalendar = availabilityDate.toISOString();
      } else {
        updateRequest.availabilityCalendar = null;
      }
      // --- End Availability Calendar Feature ---

      console.log("Form Value before submit:", this.profileForm.value); 

      // --- FormData INSPECTION LOG ---
      const formData = new FormData();

      // --- Package non-file data into profileUpdateRequestDTO part as JSON ---
      const profileUpdateRequestDTO = { 
        firstName: updateRequest.firstName,
        lastName: updateRequest.lastName,
        pincode: updateRequest.pincode,
        contactDetails: updateRequest.contactDetails,
        skills: updateRequest.skills,
        gender: updateRequest.gender,
        availabilityCalendar: updateRequest.availabilityCalendar
      };
      formData.append('profileUpdateRequestDTO', new Blob([JSON.stringify(profileUpdateRequestDTO)], { type: 'application/json' }));
      // --- End Package non-file data ---


      if (updateRequest.photoFile) formData.append('photoFile', updateRequest.photoFile, updateRequest.photoFile.name);

      console.log("FormData Payload:", formData); 

      // --- CORRECTED userService.updateUserProfile CALL - TRULY FIXED (Again!) ---
      this.userService.updateUserProfile(this.authService.getEmail() || '', updateRequest).subscribe({ // Correct method call with userEmail and *updateRequest* <--- CORRECT VARIABLE NAME IS updateRequest
      // --- END CORRECTED userService.updateUserProfile CALL ---
          next: (updatedProfile) => {
            this.loading.set(false);
            this.notificationService.showSuccess('Profile updated successfully!');
            this.router.navigate(['/profile']);
          },
          error: (error) => {
            this.loading.set(false);
            this.errorService.handleHttpError(error, 'Failed to update profile');
          }
        });
    }
  }
}