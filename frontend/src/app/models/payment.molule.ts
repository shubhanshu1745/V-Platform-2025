// src/app/models/payment.model.ts (Up-to-date)
export interface Payment {
  paymentId?: number;
  eventId?: number;
  volunteerId?: number;
  amount?: number;
  status?: string;
  transactionId?: string; 
}