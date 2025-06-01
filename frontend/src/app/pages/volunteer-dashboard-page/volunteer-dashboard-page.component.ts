import { Component, inject, signal, OnInit } from '@angular/core';
import { EventCardComponent } from '../../components/event-card/event-card.component';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../components/loading-spinner/loading-spinner.component';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.molule';
import { ErrorService } from '../../services/error.service';
import { Console } from 'console';

@Component({
  selector: 'app-volunteer-dashboard-page',
  standalone: true,
  imports: [CommonModule, EventCardComponent, LoadingSpinnerComponent],
  templateUrl: './volunteer-dashboard-page.component.html',
  styleUrl: './volunteer-dashboard-page.component.scss'
})
export class VolunteerDashboardPageComponent implements OnInit {
  appliedEvents = signal<Event[]>([]);
  loadingAppliedEvents = signal<boolean>(false);
  private eventService = inject(EventService);
  private errorService = inject(ErrorService);

  ngOnInit(): void {
    this.loadAppliedEvents();
  }

  loadAppliedEvents(): void {
    console.log("=======I came here from Like 1. Login happened and than navbar show's the link's that is for volunteer Show first Link is for the applied events getallappliedevents"); 
    console.log("=======Inside the LaodApplied Events");
    this.loadingAppliedEvents.set(true);
    console.log("===========Just before the calling the event service method====");
    this.eventService.getAppliedEventsForVolunteer().subscribe({ // Use getAppliedEventsForVolunteer
      next: (events) => {
        this.appliedEvents.set(events);
        this.loadingAppliedEvents.set(false);
      },
      
      error: (error) => {
        this.errorService.handleHttpError(error, 'Failed to load applied events');
        this.loadingAppliedEvents.set(false);
      }
    });
  }
}