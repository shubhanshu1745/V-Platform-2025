// src/app/services/error.service.ts
import { Injectable } from '@angular/core';
import { NotificationService } from './notification.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class ErrorService {

    constructor(private notificationService: NotificationService) { }

    handleHttpError(error: HttpErrorResponse, defaultMessage?: string): void {
        let errorMessage = defaultMessage || 'An unexpected error occurred. Please try again later.';

        if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
            errorMessage = error.error.message || errorMessage;
        } else {
            if (error.status === 404) {
                errorMessage = 'Resource not found.';
            } else if (error.status === 401 || error.status === 403) {
                errorMessage = 'Unauthorized access.';
            } else if (typeof error.error === 'string') {
                errorMessage = error.error;
            }
        }

        console.error('API Error:', error);
        this.notificationService.showError(errorMessage);
    }
}