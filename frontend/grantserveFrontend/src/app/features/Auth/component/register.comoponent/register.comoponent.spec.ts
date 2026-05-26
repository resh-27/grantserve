import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { RegisterService } from '../../../../core/services/register/register.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

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
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          this.isError = true;
          this.isSuccess = false;
          
          console.error('Registration Error:', err);

          // Extracting message from Spring Boot 400 error
          if (err.error) {
            if (typeof err.error === 'string') {
              this.registerMessage = err.error;
            } else if (err.error.message) {
              this.registerMessage = err.error.message;
            } else {
              this.registerMessage = 'Registration failed. Please verify your details.';
            }
          } else {
            this.registerMessage = 'Unable to connect to the server.';
          }

          this.cdr.detectChanges();
        }
      });
    }
  }

  // This is the method that was missing!
  closeAlert(): void {
    this.isError = false;
    this.registerMessage = '';
    this.cdr.detectChanges();
  }
}