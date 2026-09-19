import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-reset-request',
  templateUrl: './reset-request.component.html',
})
export class ResetRequestComponent {
  email = '';
  info = '';
  error = '';

  constructor(private http: HttpClient) {}

  async request() {
    this.info = this.error = '';
    try {
      const token = await this.http
        .post(
          `${environment.apiBaseUrl}/api/auth/request-reset?email=${encodeURIComponent(this.email)}`,
          null,
          { responseType: 'text' }
        )
        .toPromise();
      // For dev the backend returns token; in production it would send email.
      this.info = 'Se o email existir, instruções foram enviadas. (token: ' + token + ')';
    } catch (e: any) {
      this.error = 'Erro ao solicitar recuperação.';
    }
  }
}
