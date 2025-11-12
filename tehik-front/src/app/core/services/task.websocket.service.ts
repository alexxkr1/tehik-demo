import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { TaskResponseDTO } from '@/core/models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TaskWebSocketService implements OnDestroy {
  private stompClient: Client | null = null;

  private updatesSubject = new BehaviorSubject<TaskResponseDTO | null>(null);
  updates$ = this.updatesSubject.asObservable();

  private taskSubscriptions = new Map<number, StompSubscription>();

  connect(): void {
    if (this.stompClient && this.stompClient.active) {
      console.warn('WebSocket already connected');
      return;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.hostname}:8080/ws`;

    this.stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000, // retry every 5s
      debug: (msg) => console.log('[STOMP]', msg),

      onConnect: () => {
        console.log('✅ STOMP connected');
      },

      onStompError: (frame) => {
        console.error('Broker error:', frame.headers['message']);
      },
    });

    this.stompClient.activate();
  }

  subscribeToTask(taskId: number): void {
    if (!this.stompClient || !this.stompClient.active) {
      console.warn('STOMP client not connected yet');
      return;
    }

    if (this.taskSubscriptions.has(taskId)) {
      console.warn(`Already subscribed to task ${taskId}`);
      return;
    }

    const destination = `/topic/task-status/${taskId}`;
    const subscription = this.stompClient.subscribe(destination, (message: IMessage) => {
      try {
        const dto = JSON.parse(message.body) as TaskResponseDTO;
        this.updatesSubject.next(dto);
      } catch (e) {
        console.error('Error parsing STOMP message', e);
      }
    });

    this.taskSubscriptions.set(taskId, subscription);
  }

  unsubscribeFromTask(taskId: number): void {
    const subscription = this.taskSubscriptions.get(taskId);
    if (subscription) {
      subscription.unsubscribe();
      this.taskSubscriptions.delete(taskId);
    }
  }

  disconnect(): void {
    this.taskSubscriptions.forEach((sub) => sub.unsubscribe());
    this.taskSubscriptions.clear();

    if (this.stompClient && this.stompClient.active) {
      console.log('Disconnecting STOMP...');
      this.stompClient.deactivate();
      this.stompClient = null;
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
