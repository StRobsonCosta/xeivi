import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OwnerRoutingModule } from './owner-routing.module';
import { OwnerComponent } from './owner.component';

@NgModule({
  declarations: [OwnerComponent],
  imports: [CommonModule, FormsModule, OwnerRoutingModule]
})
export class OwnerModule {}
