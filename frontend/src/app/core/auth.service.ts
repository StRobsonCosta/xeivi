import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export type Role = 'CLIENTE' | 'BARBEIRO' | 'DONO';

export interface AuthUser {
  username: string;
  role: Role;
  customerId?: number;
}

interface LoginResponse {
  token: string;
  role: string;
  username?: string;
  customerId?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<AuthUser | null>(null);
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('auth.token');
    const role = localStorage.getItem('auth.role') as Role | null;
    const username = localStorage.getItem('auth.username');
    if (token && role && username) {
      this.userSubject.next({ username, role });
    }
  }

  async login(username: string, password: string, _role?: Role): Promise<boolean> {
    try {
      const res = await this.http.post<LoginResponse>(`${environment.apiBaseUrl}/api/auth/login`, { username, password }).toPromise();
      if (!res || !res.token || !res.role) return false;
      // Trust server-provided role only (prevent client spoofing)
      localStorage.setItem('auth.token', res.token);
      localStorage.setItem('auth.role', res.role);
      const serverUsername = res.username || username;
      localStorage.setItem('auth.username', serverUsername);
      if (res.customerId !== undefined && res.customerId !== null) {
        localStorage.setItem('auth.customerId', String(res.customerId));
      }
      this.userSubject.next({ username: serverUsername, role: res.role as Role, customerId: res.customerId });
      return true;
    } catch (e) {
      return false;
    }
  }

  logout(): void {
    localStorage.removeItem('auth.token');
    localStorage.removeItem('auth.role');
    localStorage.removeItem('auth.username');
    localStorage.removeItem('auth.customerId');
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

  getToken(): string | null {
    return localStorage.getItem('auth.token');
  }
}
