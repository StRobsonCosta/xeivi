import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const authHeader = this.auth.getAuthHeader();
    if (!authHeader) {
      return next.handle(req);
    }

    const secureReq = req.clone({
      setHeaders: {
        Authorization: authHeader,
        'Content-Type': 'application/json'
      }
    });
    return next.handle(secureReq);
  }
}
