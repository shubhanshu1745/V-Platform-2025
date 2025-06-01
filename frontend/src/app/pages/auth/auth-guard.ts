import { Injectable, inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service'; // Relative path

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (!this.authService.isAuthenticated()) {
      // Not authenticated, redirect to login
      return this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
    }

    const requiredRoles = route.data['roles'] as Array<string>; // Get roles from route data
    if (requiredRoles && requiredRoles.length > 0) {
      const userRoles = this.authService.getRole(); // Get user's roles from token
      if (!userRoles || !requiredRoles.some(role => userRoles.includes(role))) {
        // User is authenticated but doesn't have required role, redirect to unauthorized page (or home)
        console.warn('Unauthorized access attempt. Required roles:', requiredRoles, 'User roles:', userRoles);
        return this.router.createUrlTree(['/unauthorized']); // Or router.createUrlTree(['/']); for home
      }
    }

    return true; // Authenticated and authorized (or no role requirement)
  }
}