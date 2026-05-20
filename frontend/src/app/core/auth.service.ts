import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

export type Role = 'CLIENTE' | 'BARBEIRO' | 'DONO';

export interface AuthUser {
  username: string;
  role: Role;
  authHeader: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<AuthUser | null>(null);
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  async login(username: string, password: string, role: Role): Promise<boolean> {
    const credentials = window.btoa(`${username}:${password}`);
    const authHeader = `Basic ${credentials}`;
    const headers = new HttpHeaders({ Authorization: authHeader });

    let validateUrl = '';
    try {
      if (role === 'CLIENTE') {
        validateUrl = `${environment.apiBaseUrl}/api/clients/services`;
      } else if (role === 'BARBEIRO') {
        const date = new Date().toISOString().slice(0, 10);
        validateUrl = `${environment.apiBaseUrl}/api/barbers/schedule?date=${date}`;
      } else {
        const from = new Date(new Date().setDate(new Date().getDate() - 1)).toISOString().slice(0, 10);
        const to = new Date().toISOString().slice(0, 10);
        validateUrl = `${environment.apiBaseUrl}/api/owners/dashboard?from=${from}&to=${to}`;
      }

      await this.http.get(validateUrl, { headers }).toPromise();

      this.userSubject.next({ username, role, authHeader });
      return true;
    } catch (e) {
      return false;
    }
  }

  logout(): void {
    this.userSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.userSubject.getValue();
  }

  isClient(): boolean {
    return this.userSubject.getValue()?.role === 'CLIENTE';
  }

  isBarber(): boolean {
    return this.userSubject.getValue()?.role === 'BARBEIRO';
  }

  isOwner(): boolean {
    return this.userSubject.getValue()?.role === 'DONO';
  }

  getAuthHeader(): string | null {
    return this.userSubject.getValue()?.authHeader || null;
  }
}
