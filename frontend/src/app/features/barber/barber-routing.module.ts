import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BarberComponent } from './barber.component';

const routes: Routes = [{ path: '', component: BarberComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BarberRoutingModule {}
