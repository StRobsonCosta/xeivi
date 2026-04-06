import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { OwnerDashboard } from '../../shared/models';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.css']
})
export class OwnerComponent implements OnInit {
  dashboard: OwnerDashboard | null = null;
  from = new Date(new Date().setDate(new Date().getDate() - 7)).toISOString().slice(0, 10);
  to = new Date().toISOString().slice(0, 10);
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.error = '';
    this.api.get<OwnerDashboard>(`/api/owners/dashboard?from=${this.from}&to=${this.to}`)
      .subscribe({
        next: data => (this.dashboard = data),
        error: () => (this.error = 'Falha ao carregar dashboard.')
      });
  }
}
