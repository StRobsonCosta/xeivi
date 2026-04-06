import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

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

  login(username: string, password: string, role: Role): void {
    const credentials = window.btoa(`${username}:${password}`);
    this.userSubject.next({ username, role, authHeader: `Basic ${credentials}` });
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
