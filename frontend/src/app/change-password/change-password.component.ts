import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {
  oldPassword = '';
  newPassword = '';
  error = '';
  info = '';

  constructor(private http: HttpClient) {}

  async change() {
    this.error = this.info = '';
    try {
      await this.http.post(`${environment.apiBaseUrl}/api/users/change-password`, { oldPassword: this.oldPassword, newPassword: this.newPassword }).toPromise();
      this.info = 'Senha atualizada.';
    } catch (e:any) {
      this.error = 'Erro ao alterar senha.';
    }
  }
}
