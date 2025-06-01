import { Component, inject, signal, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.molule';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../components/loading-spinner/loading-spinner.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ErrorService } from '../../services/error.service';

@Component({
  selector: 'app-user-profile-page',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, RouterLink, RouterOutlet],
  templateUrl: './user-profile-page.component.html',
  styleUrl: './user-profile-page.component.scss'
})
export class UserProfilePageComponent implements OnInit {
  userProfile = signal<User | null>(null);
  loading = signal<boolean>(false);
  error = signal<boolean>(false);
  private userService = inject(UserService);
  private errorService = inject(ErrorService);

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.loading.set(true);
    this.error.set(false);
    this.userService.getMyProfile().subscribe({ // Use getMyProfile() - no argument
      next: (user) => {
        this.userProfile.set(user);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set(true);
        this.loading.set(false);
        this.errorService.handleHttpError(error, 'Failed to load user profile');
      }
    });
  }}