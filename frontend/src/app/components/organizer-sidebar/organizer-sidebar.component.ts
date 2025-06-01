// src/app/components/organizer-sidebar/organizer-sidebar.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-organizer-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './organizer-sidebar.component.html',
  styleUrl: './organizer-sidebar.component.scss'
})
export class OrganizerSidebarComponent {

}