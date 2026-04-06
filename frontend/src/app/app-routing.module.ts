import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SecurityGuard } from './core/security.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'cliente', loadChildren: () => import('./features/client/client.module').then(m => m.ClientModule), canActivate: [SecurityGuard] },
  { path: 'barbeiro', loadChildren: () => import('./features/barber/barber.module').then(m => m.BarberModule), canActivate: [SecurityGuard] },
  { path: 'dono', loadChildren: () => import('./features/owner/owner.module').then(m => m.OwnerModule), canActivate: [SecurityGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
