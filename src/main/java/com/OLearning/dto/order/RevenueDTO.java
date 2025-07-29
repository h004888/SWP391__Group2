package com.OLearning.dto.order;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RevenueDTO {
    private Double currentRevenue;
    private Double lastRevenue;
    private Double growth;

    public RevenueDTO(Double currentRevenue, Double lastRevenue, Double growth) {
        this.currentRevenue = currentRevenue;
        this.lastRevenue = lastRevenue;
        this.growth = growth;
    }
}