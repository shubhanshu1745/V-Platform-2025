// Corrected event-listing-page.component.ts (Component - Type Error Fix in applyFilters)
import { Component, inject, signal, OnInit } from '@angular/core';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.molule';
import { EventCardComponent } from '../../components/event-card/event-card.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoadingSpinnerComponent } from '../../components/loading-spinner/loading-spinner.component';
import { ErrorService } from '../../services/error.service';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-event-listing-page',
  standalone: true,
  imports: [CommonModule, EventCardComponent, FormsModule, LoadingSpinnerComponent],
  templateUrl: './event-listing-page.component.html',
  styleUrl: './event-listing-page.component.scss'
})
export class EventListingPageComponent implements OnInit {
  private eventService = inject(EventService);
  private errorService = inject(ErrorService);

  events = signal<Event[]>([]);
  loading = signal<boolean>(false);
  pincodeFilter: string = '';
  startDateFilter: string = '';
  endDateFilter: string = '';
  skillsFilter: string = '';
  searchFilter: string = ''; // Added searchFilter property

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.loading.set(true);
    let params = new HttpParams();

    if (this.pincodeFilter) params = params.set('pincode', this.pincodeFilter);
    if (this.startDateFilter) { // <--- ADDED NULL CHECK
      const formattedStartDate = this.formatDate(this.startDateFilter);
      if (formattedStartDate) {
        params = params.set('startDate', formattedStartDate);
      }
    }
    if (this.endDateFilter) {     // <--- ADDED NULL CHECK
      const formattedEndDate = this.formatDate(this.endDateFilter);
      if (formattedEndDate) {
        params = params.set('endDate', formattedEndDate);
      }
    }
    if (this.skillsFilter) params = params.set('skills', this.skillsFilter);
    if (this.searchFilter) params = params.set('query', this.searchFilter);

    this.eventService.getAllEvents(
      params.get('pincode') || undefined,
      params.get('startDate') || undefined,
      params.get('endDate') || undefined,
      params.get('skills') || undefined,
      params.get('query') || undefined
    ).subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorService.handleHttpError(error, 'Failed to load events with filters');
        this.loading.set(false);
      }
    });
  }

  // Helper function to format date to ISO string (YYYY-MM-DD) for backend
  private formatDate(dateString: string): string | null {
    if (!dateString) {
      return null;
    }
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      console.warn(`Invalid date string for filtering: ${dateString}`);
      return null;
    }
    return date.toISOString().split('T')[0]; // Format to YYYY-MM-DD (ISO 8601 date part)
  }
}