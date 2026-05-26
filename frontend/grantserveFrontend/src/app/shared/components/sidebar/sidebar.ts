import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink,RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
  

export class Sidebar {
 
  constructor(private router: Router, private toast: ToastService) {}

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    this.toast.show('Logout successful', 'success');
    this.router.navigate(['/']); // Sends them away
  }

 
}
