import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class JwtService {
  private getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.userId ? Number(decoded.userId) : null;
    } catch (error) {
      console.error("Token decoding failed", error);
      return null;
    }
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      // Returns 'MANAGER' based on your provided payload
      return decoded.scope || null;
    } catch (error) {
      console.error("Failed to decode user role", error);
      return null;
    }
  }
}
