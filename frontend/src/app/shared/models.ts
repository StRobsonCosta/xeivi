export interface ServiceOffer {
  id: number;
  name: string;
  description: string;
  price: number;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
}

export interface AppointmentRequest {
  customerId: number;
  serviceOfferId: number;
  scheduledAt: string;
  ownerSharePercentage: number;
}

export interface AppointmentResponse {
  appointmentId: number;
  customerId: number;
  serviceName: string;
  scheduledAt: string;
  totalPrice: number;
  ownerShare: number;
  barberShare: number;
}

export interface DailyMovement {
  date: string;
  appointments: number;
  revenue: number;
}

export interface OwnerDashboard {
  totalCustomers: number;
  grossRevenue: number;
  ownerProfit: number;
  totalCosts: number;
  busiestDays: DailyMovement[];
  slowestDays: DailyMovement[];
}

export interface BarberReport {
  dailyEarnings: number;
  projection: Record<string, number>;
}
