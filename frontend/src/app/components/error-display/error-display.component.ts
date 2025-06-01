import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-display',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-display.component.html',
  styleUrl: './error-display.component.scss'
})
export class ErrorDisplayComponent {
  /**
   * The error message to display.
   * This is a required input property.
   */
  @Input() errorMessage: string | null = null; // Input property for the error message

  /**
   * Optional property to control whether to display a dismiss button.
   */
  @Input() showDismissButton: boolean = false;

  /**
   * Emits an event when the dismiss button is clicked.
   * Parent components can subscribe to this event to handle the dismissal.
   */
  // @Output() dismiss = new EventEmitter<void>(); //Removed EventEmitter as not needed

  /**
   * Handles the dismiss button click.
   * Emits the 'dismiss' event to notify the parent component.
   */
  // onDismiss(): void { //Removed onDismiss() and EventEmitter as not needed
  //   this.dismiss.emit();
  // }
}