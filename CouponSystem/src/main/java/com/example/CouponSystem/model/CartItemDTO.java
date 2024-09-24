package com.example.CouponSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemDTO {
    private Long productId;
    private int quantity;
    private double price;
    private double totalDiscount;
}