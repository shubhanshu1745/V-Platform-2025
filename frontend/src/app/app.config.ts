import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS, provideHttpClient } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AuthGuard } from './pages/auth/auth-guard';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { MatSnackBarModule } from '@angular/material/snack-bar'; // Import MatSnackBarModule

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(),
    importProvidersFrom(
      HttpClientModule, 
      ReactiveFormsModule, 
      NgbModule, 
      MatIconModule, 
      MatDatepickerModule, 
      MatInputModule,
      MatSnackBarModule // <--- ADD MatSnackBarModule here
    ),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    AuthGuard,
  ],
};