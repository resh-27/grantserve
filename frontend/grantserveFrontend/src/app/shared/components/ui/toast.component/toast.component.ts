import { Component, OnInit } from '@angular/core';
import { ToastService, ToastMessage } from '../../../services/toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (toastService.toastState$ | async; as toast) {
      <div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 1100">
        <div class="toast show align-items-center text-white border-0" 
             [ngClass]="'bg-' + toast.type" role="alert">
          <div class="d-flex">
            <div class="toast-body">
              <i class="bi" [ngClass]="getIcon(toast.type)"></i> {{ toast.message }}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" (click)="close()"></button>
          </div>
        </div>
      </div>
    }
  `
})
export class ToastComponent {

  constructor(public toastService: ToastService) {}

  getIcon(type: string) {
    switch(type) {
      case 'success': return 'bi-check-circle-fill';
      case 'danger': return 'bi-exclamation-triangle-fill';
      default: return 'bi-info-circle-fill';
    }
  }

  close() {
    this.toastService.clear();
  }
}