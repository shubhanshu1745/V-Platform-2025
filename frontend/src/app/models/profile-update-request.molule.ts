// profile-update-request.molule.ts
export interface ProfileUpdateRequest {
    firstName?: string;
    lastName?: string;
    pincode?: string;
    contactDetails?: string;
    photoFile?: File | null; // For file upload
    skills?: string;
    gender?: string;
  
    // --- Volunteer Availability Calendar Feature ---
    availabilityCalendar?: string | null;
    // --- End Volunteer Availability Calendar Feature ---
  }