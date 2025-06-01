// auth-request.molule.ts
export interface AuthRequest {
  email?: string;
  password?: string;
  firstName?: string; // Only for registration
  lastName?: string;  // Only for registration
  pincode?: string;   // Only for registration
}