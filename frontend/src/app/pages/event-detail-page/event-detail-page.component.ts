import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.molule';
import { CommonModule, DatePipe } from '@angular/common';
import { LoadingSpinnerComponent } from '../../components/loading-spinner/loading-spinner.component';
import { ErrorService } from '../../services/error.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-event-detail-page',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, MatIconModule, MatButtonModule, DatePipe, MatProgressSpinnerModule],
  templateUrl: './event-detail-page.component.html',
  styleUrls: ['./event-detail-page.component.scss']
})
export class EventDetailPageComponent implements OnInit {
  eventId: number = 0;
  event = signal<Event | null>(null);
  loading = signal<boolean>(false);
  applying = signal<boolean>(false);
  private route = inject(ActivatedRoute);
  private eventService = inject(EventService);
  private errorService = inject(ErrorService);
  authService = inject(AuthService);
  private notificationService = inject(NotificationService);

  requiredSkillsList = computed(() => {
    const evt = this.event();
    return evt?.requiredSkills ? evt.requiredSkills.split(',') : [];
  });

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('eventId'));
    if (this.eventId) {
      this.loadEventDetails(this.eventId);
    } else {
      console.error('Invalid event ID');
    }
  }

  loadEventDetails(eventId: number): void {
    this.loading.set(true);
    this.eventService.getEvent(eventId).subscribe({
      next: (evt) => {
        this.event.set(evt);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorService.handleHttpError(error, 'Failed to load event details');
        this.loading.set(false);
      }
    });
  }

  applyForEvent(): void {
    if (this.eventId) {
      this.applying.set(true);
      this.eventService.applyForEvent(this.eventId).subscribe({
        next: () => {
          this.applying.set(false);
          this.notificationService.showSuccess('Application submitted successfully!');
        },
        error: (error) => {
          this.applying.set(false);
          this.errorService.handleHttpError(error, 'Failed to apply for event');
        }
      });
    }
  }

  canApply(): boolean {
    return this.authService.isAuthenticatedUser() && this.event() !== null;
  }
}
