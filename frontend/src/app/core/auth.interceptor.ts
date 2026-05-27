import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // don't override if request already has Authorization
    if (req.headers.has('Authorization')) {
      return next.handle(req);
    }

    const token = this.auth.getToken();
    if (!token) return next.handle(req);

    const setHeaders: Record<string, string> = { Authorization: `Bearer ${token}` };
    // only set Content-Type for non-GET requests when it's not already present
    if (!req.headers.has('Content-Type') && req.method !== 'GET') {
      setHeaders['Content-Type'] = 'application/json';
    }

    const secureReq = req.clone({ setHeaders });
    return next.handle(secureReq);
  }
}
