// src/app/components/navbar/navbar.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { RoleType } from '../../../models/role-type.enum.enum';
import { NotificationService } from '../../../services/notification.service';
import { ErrorService } from '../../../services/error.service';
import { NgbCollapse, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  standalone: true,
  imports: [NgbDropdownModule, NgbCollapse, RouterLinkActive, NgIf, CommonModule, RouterLink],
})
export class NavbarComponent {
  authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private errorService = inject(ErrorService);

  currentRole = signal<RoleType>(RoleType.VOLUNTEER);
  isNavbarCollapsed = true;
  roleType = RoleType;

  constructor() {
    this.currentRole.set(RoleType.VOLUNTEER);
  }

  isAuthenticatedUser(): boolean {
    return this.authService.isAuthenticated();
  }

  isOrganizerRole(): boolean {
    return this.currentRole() === RoleType.ORGANIZER;
  }

  onRoleToggle(newRole: RoleType): void {
    if (this.authService.isAuthenticatedUser()) {
      this.authService.switchRole(RoleType[newRole]).subscribe({
        next: (response: any) => {
          this.currentRole.set(newRole);
          this.notificationService.showSuccess(`Switched to ${newRole} view`);
          this.router.navigate(['/']); // Navigate to home or dashboard after role switch
        },
        error: (error: any) => {
          this.errorService.handleHttpError(error, 'Role switch failed');
        }
      });
    } else {
      this.notificationService.showError("You must be logged in to switch roles.");
    }
  }

  logout(): void {
    this.authService.logout();
    this.notificationService.showSuccess('Logged out successfully');
  }

  closeNavbar(): void {
    this.isNavbarCollapsed = true;
  }
}