import { HttpRequest, HttpHandlerFn, HttpInterceptorFn, HttpEvent, HttpInterceptor, HttpHandler } from '@angular/common/http'; // Import HttpHandlerFn
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';



@Injectable()
export class JwtInterceptor implements HttpInterceptor 
{
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> 
  {
    let token = localStorage.getItem('jwtToken');
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(request);
  }
 }