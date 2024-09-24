package com.example.CouponSystem.model;

import com.example.CouponSystem.service.JsonToMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> discountDetails;

    private LocalDate expiryDate;

    private Boolean isActive;
}