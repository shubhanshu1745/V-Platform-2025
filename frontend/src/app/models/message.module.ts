// message.module.ts
import { Chat } from './chat.module';
import { User } from './user.molule';

export interface Message {
  messageId?: number;
  chatId?: number;
  senderUserId?: number;
  content?: string;
  sentAt?: Date;
  chat?: Chat;       // Optional: Include full Chat object if needed
  sender?: User;     // Optional: Include full User object if needed
}