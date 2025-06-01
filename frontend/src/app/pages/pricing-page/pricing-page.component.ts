import { Component } from '@angular/core';

@Component({
  selector: 'app-pricing-page',
  imports: [],
  template: `
    <div class="container pricing-page mt-4 page-content">
  <h2>Pricing Plans</h2>
  <p class="lead text-center mb-5">Choose the plan that best fits your needs.</p>

  <div class="row row-cols-1 row-cols-md-3 g-4">
    <div class="col">
      <div class="card pricing-card free-plan">
        <div class="card-header">
          <h4 class="my-0 fw-normal">Free</h4>
        </div>
        <div class="card-body">
          <h1 class="card-title pricing-card-title">$0<small class="text-muted fw-light">/month</small></h1>
          <ul class="list-unstyled mt-3 mb-4">
            <li>Up to 3 Events per Month</li>
            <li>Up to 25 Volunteers per Event</li>
            <li>Basic Event Management</li>
            <li>Basic Volunteer Search</li>
            <li>Community Forum Support</li>
          </ul>
          <button type="button" class="w-100 btn btn-lg btn-outline-primary">Sign up for Free</button>
        </div>
      </div>
    </div>

    <div class="col">
      <div class="card pricing-card essential-plan">
        <div class="card-header">
          <h4 class="my-0 fw-normal">Essential</h4>
        </div>
        <div class="card-body">
          <h1 class="card-title pricing-card-title">$29<small class="text-muted fw-light">/month</small></h1>
          <ul class="list-unstyled mt-3 mb-4">
            <li>Up to 20 Events per Month</li>
            <li>Up to 100 Volunteers per Event</li>
            <li>Advanced Volunteer Search</li>
            <li>Enhanced Management Tools</li>
            <li>Email Support</li>
          </ul>
          <button type="button" class="w-100 btn btn-lg btn-primary">Get started</button>
        </div>
      </div>
    </div>

    <div class="col">
      <div class="card pricing-card professional-plan">
        <div class="card-header">
          <h4 class="my-0 fw-normal">Professional</h4>
        </div>
        <div class="card-body">
          <h1 class="card-title pricing-card-title">$79<small class="text-muted fw-light">/month</small></h1>
          <ul class="list-unstyled mt-3 mb-4">
            <li>Unlimited Events</li>
            <li>Unlimited Volunteers</li>
            <li>Advanced Reporting & Analytics</li>
            <li>Priority Support</li>
            <li>Remove Platform Branding</li>
          </ul>
          <button type="button" class="w-100 btn btn-lg btn-primary">Contact us</button>
        </div>
      </div>
    </div>
  </div>
</div>
  `,
  styleUrl: './pricing-page.component.scss'
})
export class PricingPageComponent {

}
