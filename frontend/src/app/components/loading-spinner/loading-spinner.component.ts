// src/app/components/loading-spinner/loading-spinner.component.ts
import { Component, Input, signal } from '@angular/core'; // Import Input
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-spinner.component.html',
  styleUrl: './loading-spinner.component.scss'
})
export class LoadingSpinnerComponent {
  @Input() diameter: number = 40; // <--- ADDED @Input() diameter with a default value (e.g., 40)
  isLoading = signal<boolean>(true); // You likely already have this
}