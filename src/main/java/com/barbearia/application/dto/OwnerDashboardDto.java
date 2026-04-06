package com.barbearia.application.dto;

import java.time.LocalDate;
import java.util.List;

public class OwnerDashboardDto {

    private long totalCustomers;
    private double grossRevenue;
    private double ownerProfit;
    private double totalCosts;
    private List<DailyMovement> busiestDays;
    private List<DailyMovement> slowestDays;

    public OwnerDashboardDto(long totalCustomers, double grossRevenue, double ownerProfit, double totalCosts,
                             List<DailyMovement> busiestDays, List<DailyMovement> slowestDays) {
        this.totalCustomers = totalCustomers;
        this.grossRevenue = grossRevenue;
        this.ownerProfit = ownerProfit;
        this.totalCosts = totalCosts;
        this.busiestDays = busiestDays;
        this.slowestDays = slowestDays;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public double getGrossRevenue() {
        return grossRevenue;
    }

    public double getOwnerProfit() {
        return ownerProfit;
    }

    public double getTotalCosts() {
        return totalCosts;
    }

    public List<DailyMovement> getBusiestDays() {
        return busiestDays;
    }

    public List<DailyMovement> getSlowestDays() {
        return slowestDays;
    }

    public static class DailyMovement {
        private LocalDate date;
        private long appointments;
        private double revenue;

        public DailyMovement(LocalDate date, long appointments, double revenue) {
            this.date = date;
            this.appointments = appointments;
            this.revenue = revenue;
        }

        public LocalDate getDate() {
            return date;
        }

        public long getAppointments() {
            return appointments;
        }

        public double getRevenue() {
            return revenue;
        }
    }
}
