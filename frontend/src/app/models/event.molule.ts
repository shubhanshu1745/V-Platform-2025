// src/app/models/event.model.ts
export interface Event {
  eventId?: number;
  organizerId?: number;
  title?: string;
  description?: string;
  startDate?: string | null;
  endDate?: string | null;
  location?: string;
  requiredSkills?: string;
  volunteerCapacity?: number;
  paymentAmount?: number;
  pincode?: string;
  photoBase64?: string; // Base64 encoded photo (for display)
  lastRegistrationDate?: string | null;
  contacts?: string;
  photoFile?: File | null; // Corrected to allow null
}