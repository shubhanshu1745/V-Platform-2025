// src/app/models/attendance.model.ts (Up-to-date)
export interface Attendance {
  attendanceId?: number;
  eventId: number;
  volunteerId: number;
  checkInTime?: string; 
  checkOutTime?: string; 
}