// review.molule.ts
import { ReviewType } from './review-type.enum';

export interface Review {
  reviewId?: number;
  reviewerId?: number;
  revieweeId?: number;
  rating?: number;
  comment?: string;
  reviewType?: ReviewType;
  createdAt?: string; // Consider using Date or string in ISO format
}