import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { AppointmentRequest, AppointmentResponse, Product, ServiceOffer } from '../../shared/models';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class ClientComponent implements OnInit {
  services: ServiceOffer[] = [];
  products: Product[] = [];
  request: AppointmentRequest = {
    customerId: 1,
    serviceOfferId: 0,
    scheduledAt: new Date().toISOString().slice(0, 16),
    ownerSharePercentage: 25
  };
  scheduleResponse: AppointmentResponse | null = null;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<ServiceOffer[]>('/api/clients/services').subscribe({
      next: values => (this.services = values),
      error: () => (this.error = 'Não foi possível carregar os serviços.')
    });

    this.api.get<Product[]>('/api/clients/products').subscribe({
      next: values => (this.products = values),
      error: () => (this.error = 'Não foi possível carregar os produtos.')
    });
  }

  submit(): void {
    this.error = '';
    if (!this.request.serviceOfferId) {
      this.error = 'Selecione um serviço.';
      return;
    }

    this.api.post<AppointmentRequest, AppointmentResponse>('/api/clients/appointments', this.request)
      .subscribe({
        next: response => {
          this.scheduleResponse = response;
        },
        error: () => (this.error = 'Falha ao agendar. Verifique se o backend está rodando.')
      });
  }
}
