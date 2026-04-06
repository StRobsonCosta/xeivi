import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, Role } from '../core/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  role: Role = 'CLIENTE';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    if (!this.username || !this.password) {
      this.error = 'Preencha usuário e senha.';
      return;
    }

    this.auth.login(this.username, this.password, this.role);
    this.error = '';

    if (this.role === 'CLIENTE') {
      this.router.navigate(['/cliente']);
    } else if (this.role === 'BARBEIRO') {
      this.router.navigate(['/barbeiro']);
    } else {
      this.router.navigate(['/dono']);
    }
  }
}
