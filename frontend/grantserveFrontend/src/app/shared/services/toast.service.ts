import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ToastMessage {
  message: string;
  type: 'success' | 'danger' | 'info' | 'warning';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastSubject = new Subject<ToastMessage | null>();
  toastState$ = this.toastSubject.asObservable();

  show(message: string, type: 'success' | 'danger' | 'info' | 'warning' = 'success') {
    this.toastSubject.next({ message, type });
    
    // Auto-hide after 3 seconds
    setTimeout(() => {
      this.toastSubject.next(null);
    }, 3000);
  }

  clear() {
    this.toastSubject.next(null);
  }
}