// src/app/models/application.model.ts (Up-to-date)
import { ApplicationStatus } from './application-status.enum';
import { User } from './user.molule';// Corrected import to .model.ts

export interface Application {
  applicationId?: number;
  eventId: number;
  volunteerId: number;
  status?: ApplicationStatus;
  appliedAt?: Date;
  volunteer?: User | null;
}