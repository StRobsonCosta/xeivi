import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export type Role = 'CLIENTE' | 'BARBEIRO' | 'DONO';

export interface AuthUser {
  username: string;
  role: Role;
}

interface LoginResponse {
  token: string;
  role: string;
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
      localStorage.setItem('auth.username', username);
      this.userSubject.next({ username, role: res.role as Role });
      return true;
    } catch (e) {
      return false;
    }
  }

  logout(): void {
    localStorage.removeItem('auth.token');
    localStorage.removeItem('auth.role');
    localStorage.removeItem('auth.username');
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
