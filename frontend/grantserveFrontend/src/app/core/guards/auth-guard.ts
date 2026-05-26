import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginService } from '../services/login/login.service';
import { JwtService } from '../services/jwtService/jwt-service';

export const authGuard: CanActivateFn = (route, state) => {
  const loginService = inject(LoginService);
  const router = inject(Router);
  const jwtService = inject(JwtService);

  const allowedRoles = route.data['roles'] as string[];
  const userRole = jwtService.getUserRole();

  // 1. Check if user is logged in
  if (!loginService.isLoggedIn() || userRole === null) {
    return router.parseUrl('/');
  }

  // 2. If the route is restricted by roles
  if (allowedRoles) {
    // Normalize case to ensure "MANAGER" matches "manager" or "MANAGER"
    const hasRequiredRole = userRole && allowedRoles.some(role => 
      role.toUpperCase() === userRole.toUpperCase()
    );

    if (hasRequiredRole) {
      return true;
    } else {
      // User is logged in but has the WRONG role
      // Redirect them to their specific dashboard instead of login
      loginService.redirectBasedOnRole(userRole);
      return false; // Prevent access to the restricted route
    }
  }

  // 3. If no roles are defined but they are logged in, allow access
  return true;
};