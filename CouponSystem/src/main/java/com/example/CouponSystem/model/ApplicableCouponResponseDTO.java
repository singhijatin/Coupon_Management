package com.example.CouponSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicableCouponResponseDTO {
    private Long id;
    private String type;
    private double discount;
}
