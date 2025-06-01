import { Routes } from '@angular/router';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { LoginPageComponent } from './pages/auth/login-page/login-page.component';
import { RegistrationPageComponent } from './pages/auth/registration-page/registration-page.component';
import { OtpVerificationPageComponent } from './pages/auth/otp-verification-page/otp-verification-page.component';
import { EventListingPageComponent } from './pages/event-listing-page/event-listing-page.component';
import { VolunteerDashboardPageComponent } from './pages/volunteer-dashboard-page/volunteer-dashboard-page.component';
import { EventDetailPageComponent } from './pages/event-detail-page/event-detail-page.component';
import { UserProfilePageComponent } from './pages/user-profile-page/user-profile-page.component';
import { EditProfilePageComponent } from './pages/user-profile-page/edit-profile-page/edit-profile-page.component';
import { AuthGuard } from './pages/auth/auth-guard';
import { TermsPageComponent } from './pages/terms-page/terms-page.component';
import { PrivacyPageComponent } from './pages/privacy-page/privacy-page.component';
import { FaqPageComponent } from './pages/faq-page/faq-page.component';
import { ContactUsPageComponent } from './pages/contact-us-page/contact-us-page.component';
import { HowItWorksVolunteersPageComponent } from './pages/how-it-works-volunteers-page/how-it-works-volunteers-page.component';
import { HowItWorksOrganizersPageComponent } from './pages/how-it-works-organizers-page/how-it-works-organizers-page.component';
import { PricingPageComponent } from './pages/pricing-page/pricing-page.component';

export const routes: Routes = [
  { path: '', component: HomePageComponent, pathMatch: 'full' },
  { path: 'login', component: LoginPageComponent },
  { path: 'register', component: RegistrationPageComponent },
  { path: 'verify-otp', component: OtpVerificationPageComponent },
  { path: 'events', component: EventListingPageComponent },
  { path: 'dashboard', component: VolunteerDashboardPageComponent },
  { path: 'events/:eventId', component: EventDetailPageComponent },
  

  {
    path: 'profile',
    component: UserProfilePageComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'edit', component: EditProfilePageComponent }
    ]
  },
  {
    path: 'organizer',
    canActivate: [AuthGuard],
    data: { roles: ['ORGANIZER'] },
    children: [
      { path: 'dashboard', 
        loadComponent: () => import('./pages/organizer/organizer-dashboard-page/organizer-dashboard-page.component').then(m => m.OrganizerDashboardPageComponent) 
      },
      { path: 'create-event', 
        loadComponent: () => import('./pages/organizer/create-event-page/create-event-page.component').then(m => m.CreateEventPageComponent) 
      },
      { path: 'events/:eventId/applicants', 
        loadComponent: () => import('./pages/organizer/manage-event-applicants-page/manage-event-applicants-page.component').then(m => m.ManageEventApplicantsPageComponent) 
      },
      { path: 'events/:eventId/attendance', 
        loadComponent: () => import('./pages/organizer/record-attendance-page/record-attendance-page.component').then(m => m.RecordAttendancePageComponent) 
      },
      { path: 'events/:eventId/payments', 
        loadComponent: () => import('./pages/organizer/view-event-payments-page/view-event-payments-page.component').then(m => m.ViewEventPaymentsPageComponent) 
      },
      { path: 'volunteers/:volunteerId/payments', 
        loadComponent: () => import('./pages/organizer/view-volunteer-payments-page/view-volunteer-payments-page.component').then(m => m.ViewVolunteerPaymentsPageComponent) 
      },
      { path: 'search-volunteers', 
        loadComponent: () => import('./pages/organizer/organizer-search-volunteers-page/organizer-search-volunteers-page.component').then(m => m.OrganizerSearchVolunteersPageComponent) 
      },
    ]
  },
  // --- New Routes for Footer Links ---
  { path: 'terms', component: TermsPageComponent },
  { path: 'privacy', component: PrivacyPageComponent },
  { path: 'faq', component: FaqPageComponent },
  { path: 'contact-us', component: ContactUsPageComponent },
  { path: 'how-it-works-volunteers', component: HowItWorksVolunteersPageComponent },
  { path: 'how-it-works-organizers', component: HowItWorksOrganizersPageComponent },
  { path: 'pricing', component: PricingPageComponent },
  // ... more routes can be added later ...
];