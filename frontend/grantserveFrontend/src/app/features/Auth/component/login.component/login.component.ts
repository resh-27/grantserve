import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserResponseDto } from '../../model/login.model';
import { LoginService } from '../../../../core/services/login/login.service';
import { Router, RouterLink } from '@angular/router';
import { ToastService } from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class Login {
  loginData = { email: '', password: '' };
  isPasswordVisible: boolean = false;
  constructor(
    private loginService: LoginService,
    private router: Router,
    private toast: ToastService
  ) { }

  onLogin() {
    this.loginService.login(this.loginData).subscribe({
      next: (response: UserResponseDto) => {
        if (response.statusCode === 200) {
          // 1. Store the token and role securely
          localStorage.setItem('token', response.token);
          localStorage.setItem('userRole', response.role);
          // The '?' check prevents the 'null' error you are seeing
          if (response.userid) {
            localStorage.setItem('userId', response.userid.toString());
          } else {
            console.warn("User ID is missing from the server response");
          }
          console.log(response.message);
          this.toast.show('Login successful', 'success');
          this.loginService.redirectBasedOnRole(response.role);

        }
      },
      error: (err) => {
        // Handle 401 or 500 errors from Spring Boot
        console.error('Login failed', err);
        this.toast.show('Login Failed: ' + (err.error?.message || 'Server Error'), 'danger');
      }
    });
  }

}
