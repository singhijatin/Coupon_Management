package com.example.CouponSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private List<CartItemDTO> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
}
