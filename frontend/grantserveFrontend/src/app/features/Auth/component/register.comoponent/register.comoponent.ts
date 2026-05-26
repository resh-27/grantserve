import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { RegisterService } from '../../../../core/services/register/register.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './register.comoponent.html',
  styleUrl: './register.comoponent.css',
})
export class RegisterComoponent {
  private fb = inject(FormBuilder);
  private authService = inject(RegisterService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private toast = inject(ToastService);

  registerMessage: string = '';
  isError: boolean = false;
  isSuccess: boolean = false;

  registerForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    role: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onRegister() {
    if (this.registerForm.valid) {
      this.authService.registerUser(this.registerForm.value).subscribe({
        next: (res: any) => {
          this.isError = false;
          this.isSuccess = true;
          this.registerMessage = res.message || 'Account created successfully!';
          this.toast.show('Registration successful', 'success');
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          this.isError = true;
          this.isSuccess = false;
          
          console.error('Backend Error Payload:', err);

          const errorBody = err.error;

          if (errorBody) {
            // CASE 1: Validation Map Error { "password": "Message...", "email": "Message..." }
            if (typeof errorBody === 'object' && !errorBody.message) {
              const keys = Object.keys(errorBody);
              if (keys.length > 0) {
                // Get the first error message from the map
                const firstKey = keys[0];
                const message = errorBody[firstKey];
                // Capitalize the first letter of the key for a cleaner look
                const formattedKey = firstKey.charAt(0).toUpperCase() + firstKey.slice(1);
                this.registerMessage = `${formattedKey}: ${message}`;
              } else {
                this.registerMessage = 'Validation failed. Please check your entries.';
              }
            } 
            // CASE 2: Standard String Error
            else if (typeof errorBody === 'string') {
              this.registerMessage = errorBody;
            } 
            // CASE 3: Standard JSON Error with .message property
            else {
              this.registerMessage = errorBody.message || 'Registration failed. Try again.';
            }
          } else {
            this.registerMessage = 'Server error occurred. Please try again later.';
          }

          this.toast.show(this.registerMessage, 'danger');
          this.cdr.detectChanges();
        }
      });
    }
  }

  closeAlert(): void {
    this.isError = false;
    this.registerMessage = '';
    this.cdr.detectChanges();
  }

  goToLogin(): void {
    this.router.navigate(['/']);
  }
}