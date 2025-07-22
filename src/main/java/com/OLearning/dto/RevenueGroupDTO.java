package com.OLearning.dto;

public class RevenueGroupDTO {
    private String label;
    private Double revenue;

    public RevenueGroupDTO(String label, Double revenue) {
        this.label = label;
        this.revenue = revenue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
} 