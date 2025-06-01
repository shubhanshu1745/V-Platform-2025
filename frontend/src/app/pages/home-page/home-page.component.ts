// src/app/pages/home-page/home-page.component.ts
import { Component, inject, signal, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EventCardComponent } from "../../components/event-card/event-card.component";
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from "../../components/loading-spinner/loading-spinner.component";
import { EventService } from '../../services/event.service';
import { ErrorService } from '../../services/error.service';
import { Event } from '../../models/event.molule';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    EventCardComponent,
    CommonModule,
    LoadingSpinnerComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss'
})
export class HomePageComponent implements OnInit {

  private router = inject(Router);
  private eventService = inject(EventService);
  private errorService = inject(ErrorService);
  private authService = inject(AuthService);
  events = signal<Event[]>([]);
  loading = signal<boolean>(false);
  pincodeFilter: string = '';
  searchFilter: string = '';

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading.set(true);
    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorService.handleHttpError(error, 'Failed to load events');
        this.loading.set(false);
      }
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }


  isLoggedIn() {
      return this.authService.isAuthenticated();
    }


  applyFilters(): void {
    this.loading.set(true);
    this.eventService.getAllEvents(
      this.pincodeFilter,
      undefined,
      undefined,
      undefined,
      this.searchFilter
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
}