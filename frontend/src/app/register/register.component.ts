import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  role = 'CLIENTE';
  error = '';

  constructor(private http: HttpClient, private router: Router) {}

  async register() {
    this.error = '';
    try {
      await this.http.post(`${environment.apiBaseUrl}/api/auth/register`, { username: this.username, email: this.email, password: this.password, role: this.role }).toPromise();
      this.router.navigate(['/login']);
    } catch (e:any) {
      this.error = e?.error || 'Erro no cadastro';
    }
  }
}
