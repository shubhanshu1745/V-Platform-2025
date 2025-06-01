// src/app/layout/layout.component.ts
import { Component, inject } from '@angular/core'; // Import inject
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { FooterComponent } from '../footer/footer.component';
import { OrganizerSidebarComponent } from '../../organizer-sidebar/organizer-sidebar.component';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, FooterComponent, OrganizerSidebarComponent], // Make sure OrganizerSidebarComponent is imported
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent {
  authService = inject(AuthService); // Inject AuthService using inject() <--- INJECT AuthService

}