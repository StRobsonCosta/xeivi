import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { AppointmentResponse, BarberReport } from '../../shared/models';

@Component({
  selector: 'app-barber',
  templateUrl: './barber.component.html',
  styleUrls: ['./barber.component.css']
})
export class BarberComponent implements OnInit {
  schedule: AppointmentResponse[] = [];
  earnings = 0;
  projection: Record<string, number> = {};
  date = new Date().toISOString().slice(0, 10);
  endDate = this.date;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadSchedule();
  }

  loadSchedule(): void {
    this.error = '';
    this.api.get<AppointmentResponse[]>(`/api/barbers/schedule?date=${this.date}`)
      .subscribe({
        next: values => (this.schedule = values),
        error: () => (this.error = 'Erro ao carregar a agenda.')
      });
  }

  loadEarnings(): void {
    this.error = '';
    this.api.get<BarberReport>(`/api/barbers/earnings?date=${this.date}&until=${this.endDate}`)
      .subscribe({
        next: value => {
          this.earnings = value.dailyEarnings;
          this.projection = value.projection;
        },
        error: () => (this.error = 'Erro ao carregar os ganhos.')
      });
  }
}
