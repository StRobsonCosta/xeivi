import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-reset',
  templateUrl: './reset.component.html',
})
export class ResetComponent {
  token = '';
  newPassword = '';
  error = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  async reset() {
    this.error = '';
    try {
      await this.http
        .post(`${environment.apiBaseUrl}/api/auth/reset`, {
          token: this.token,
          newPassword: this.newPassword,
        })
        .toPromise();
      this.router.navigate(['/login']);
    } catch (e: any) {
      this.error = 'Erro ao redefinir senha.';
    }
  }
}
