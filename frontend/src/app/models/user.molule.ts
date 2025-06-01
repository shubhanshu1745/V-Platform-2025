// src/app/models/user.model.ts (Up-to-date)
import { RoleType } from "./role-type.enum.enum"; // Corrected import to .enum.ts

export interface User {
  userId?: number;
  email?: string;
  firstName?: string;
  lastName?: string;
  currentRole?: RoleType;
  pincode?: string;
  contactDetails?: string;
  photoBase64?: string; // Base64 encoded photo string
  skills?: string;
  gender?: string;
  activated?: boolean | null;
  availabilityCalendar?: any;
}