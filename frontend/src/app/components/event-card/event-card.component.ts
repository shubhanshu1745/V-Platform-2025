// src/app/components/event-card/event-card.component.ts
import { Component, Input, effect, inject, signal } from '@angular/core';
import { Event } from '../../models/event.molule';
import { CommonModule } from '@angular/common';
import { RouterLinkActive, RouterLink } from '@angular/router';
import { FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { RoleType } from '../../models/role-type.enum.enum';
import { MatIconModule } from '@angular/material/icon'; // Import MatIconModule

@Component({
  selector: 'app-event-card',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule], // Add MatIconModule to imports
  templateUrl: './event-card.component.html',
  styleUrl: './event-card.component.scss'
})
export class EventCardComponent {
  @Input() event: Event | undefined;
  authService = inject(AuthService);
  currentRole = signal<RoleType | null>(null);

  constructor() {
    // Use effect to react to changes in currentRole signal from AuthService
    effect(() => {
      const role = this.authService.currentRole();
      this.currentRole.set(role);
    });
  }

  isOrganizerEvent(): boolean {
    return this.currentRole() === RoleType.ORGANIZER && this.event?.organizerId === this.getCurrentUserId();
  }

  private getCurrentUserId(): number | null {
    // Replace with your actual logic to get the current user's ID from AuthService or token
    const userEmail = this.authService.getEmail(); // Assuming AuthService has getEmail() method
    // You'll likely need to map email to userId on frontend or have userId in token
    return 1; // Placeholder - replace with actual user ID retrieval
  }
}