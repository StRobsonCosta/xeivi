import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BarberRoutingModule } from './barber-routing.module';
import { BarberComponent } from './barber.component';

@NgModule({
  declarations: [BarberComponent],
  imports: [CommonModule, FormsModule, BarberRoutingModule]
})
export class BarberModule {}
