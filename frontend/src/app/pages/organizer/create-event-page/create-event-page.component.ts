// src/app/pages/organizer/create-event-page/create-event-page.component.ts
import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../services/event.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoadingSpinnerComponent } from '../../../components/loading-spinner/loading-spinner.component';
import { Event } from '../../../models/event.molule';
import { finalize } from 'rxjs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { toBase64 } from '../../../utils/tobase64';

@Component({
    selector: 'app-create-event-page',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, LoadingSpinnerComponent, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
    templateUrl: './create-event-page.component.html',
    styleUrl: './create-event-page.component.scss'
})
export class CreateEventPageComponent implements OnInit {
    eventForm: FormGroup;
    loading = signal<boolean>(false);
    photoPreview: string | null = null;
    selectedFile: File | null = null;
    private fb = inject(FormBuilder);
    private eventService = inject(EventService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private errorService = inject(ErrorService);

    constructor() {
        this.eventForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            startDate: ['', Validators.required],
            endDate: ['', Validators.required],
            lastRegistrationDate: [null],
            location: ['', Validators.required],
            requiredSkills: [''],
            volunteerCapacity: [0, Validators.min(0)],
            paymentAmount: [0, Validators.min(0)],
            contacts: ['', Validators.email], // <--- Added email validator for contacts
            photoFile: [null],
            pincode: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        // Initialization logic here if needed
    }

    onFileChange(event: any): void {
        this.selectedFile = event.target.files[0];
        if (this.selectedFile) {
            toBase64(this.selectedFile).then((base64String: string) => {
                this.photoPreview = base64String;
            });
            this.eventForm.patchValue({ photoFile: this.selectedFile }); // Patch value for photoFile
        } else {
            this.photoPreview = null;
            this.eventForm.patchValue({ photoFile: null }); // Patch value null if no file selected
        }
    }

    // src/app/pages/organizer/create-event-page/create-event-page.component.ts
onSubmit(): void {
    if (this.eventForm.valid) {
      this.loading.set(true);
  
      // Convert the form values to a JSON object.
      const eventPayload: any = { ...this.eventForm.value };
  
      // If a file is selected, convert it to a Base64 string.
      if (this.selectedFile) {
        toBase64(this.selectedFile).then((base64String: string) => {
          eventPayload.photoBase64 = base64String;
          this.sendEvent(eventPayload);
        });
      } else {
        this.sendEvent(eventPayload);
      }
    } else {
      this.notificationService.showError('Please fill in all required fields correctly.');
    }
  }
  
  private sendEvent(payload: any): void {
    this.eventService.createEvent(payload).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (eventResponse) => {
        this.notificationService.showSuccess('Event created successfully!');
        this.router.navigate(['/events', eventResponse.eventId]);
      },
      error: (error) => {
        this.errorService.handleHttpError(error, 'Failed to create event');
      }
    });
  }
  

    private formatDateTime(dateTimeLocalValue: any): string | null {
        if (!dateTimeLocalValue) {
            return null;
        }
        try {
            return new Date(dateTimeLocalValue).toISOString();
        } catch (error) {
            console.error("Error formatting date:", error);
            return null;
        }
    }
}