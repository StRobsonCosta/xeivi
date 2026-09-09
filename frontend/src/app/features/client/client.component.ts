import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import {
  AppointmentRequest,
  AppointmentResponse,
  Product,
  ServiceOffer,
  Barber,
} from '../../shared/models';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css'],
})
export class ClientComponent implements OnInit {
  services: ServiceOffer[] = [];
  products: Product[] = [];
  barbers: Barber[] = [];
  availableSlots: string[] = [];
  request: AppointmentRequest = {
    customerId: 0,
    serviceOfferId: 0,
    barberId: undefined,
    scheduledAt: new Date().toISOString().slice(0, 16),
    ownerSharePercentage: 25,
  };
  scheduleResponse: AppointmentResponse | null = null;
  error = '';

  constructor(
    private api: ApiService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const storedCustomer = localStorage.getItem('auth.customerId');
    if (storedCustomer) {
      this.request.customerId = Number(storedCustomer);
    }

    this.api.get<ServiceOffer[]>('/api/clients/services').subscribe({
      next: (values) => (this.services = values),
      error: () => (this.error = 'Não foi possível carregar os serviços.'),
    });

    this.api.get<Product[]>('/api/clients/products').subscribe({
      next: (values) => (this.products = values),
      error: () => (this.error = 'Não foi possível carregar os produtos.'),
    });

    this.api.get<Barber[]>('/api/clients/barbers').subscribe({
      next: (values) => (this.barbers = values),
      error: () => (this.error = 'Não foi possível carregar os barbeiros.'),
    });
  }

  onBarberOrDateChange(): void {
    if (!this.request.barberId || !this.request.scheduledAt) return;
    const date = this.request.scheduledAt.slice(0, 10);
    this.api
      .get<string[]>(`/api/clients/availability?barberId=${this.request.barberId}&date=${date}`)
      .subscribe({
        next: (values) => (this.availableSlots = values),
        error: () => (this.error = 'Erro ao carregar horários disponíveis.'),
      });
  }

  pickSlot(slot: string): void {
    // slot is ISO local date-time
    this.request.scheduledAt = slot.slice(0, 16);
  }

  submit(): void {
    this.error = '';
    if (!this.request.serviceOfferId) {
      this.error = 'Selecione um serviço.';
      return;
    }

    this.api
      .post<AppointmentRequest, AppointmentResponse>('/api/clients/appointments', this.request)
      .subscribe({
        next: (response) => {
          this.scheduleResponse = response;
        },
        error: () => (this.error = 'Falha ao agendar. Verifique se o backend está rodando.'),
      });
  }
}
