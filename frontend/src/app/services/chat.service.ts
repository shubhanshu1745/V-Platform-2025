// chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chat } from '../models/chat.module';
import { Message } from '../models/message.module';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = environment.apiUrl + '/chats'; // e.g., 'http://localhost:8080/api/v1/chats'
  private messageApiUrl = environment.apiUrl + '/messages'; // Separate URL for messages

  constructor(private http: HttpClient) { }

  getChatById(chatId: number): Observable<Chat> {
    return this.http.get<Chat>(`${this.apiUrl}/${chatId}`);
  }

  getMyChats(): Observable<Chat[]> {
    return this.http.get<Chat[]>(`${this.apiUrl}`); // GET /api/v1/chats (for logged-in user's chats)
  }

  createChat(participantUserId: number): Observable<Chat> {
    return this.http.post<Chat>(`${this.apiUrl}/create/${participantUserId}`, {});
  }

  getMessagesForChat(chatId: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.messageApiUrl}/chat/${chatId}`); // GET /api/v1/messages/chat/{chatId}
  }

  sendMessage(chatId: number, content: string): Observable<Message> {
    return this.http.post<Message>(`${this.messageApiUrl}/send/${chatId}`, content); // POST /api/v1/messages/send/{chatId}
  }
}