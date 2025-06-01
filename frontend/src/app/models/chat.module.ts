// chat.module.ts
import { Message } from "./message.module";
import { User } from './user.molule';

export interface Chat {
  chatId?: number;
  initiatorUserId?: number;
  participantUserId?: number;
  startedAt?: Date;
  messages?: Message[]; // Assuming you might want to include messages in the Chat DTO
  initiator?: User;     // Optional: Include full User objects if needed
  participant?: User;   // Optional: Include full User objects if needed
}