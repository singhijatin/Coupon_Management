package com.example.CouponSystem.service;

import com.example.CouponSystem.exception.ResourceNotFoundException;
import com.example.CouponSystem.model.*;
import com.example.CouponSystem.repo.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id " + id));
    }

    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = getCouponById(id);
        coupon.setDiscountDetails(couponDetails.getDiscountDetails());
        coupon.setExpiryDate(couponDetails.getExpiryDate());
        coupon.setIsActive(couponDetails.getIsActive());
        coupon.setType(couponDetails.getType());
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }

    public List<ApplicableCouponResponseDTO> getApplicableCoupons(CartDTO cartDTO) {

        List<ApplicableCouponResponseDTO> applicableCoupons = new ArrayList<>();
        double totalPrice = calculateCartTotal(cartDTO);

        List<Coupon> coupons = couponRepository.findAll();

        for (Coupon coupon : coupons) {
            double discount = 0;
            if (coupon.getType().equals(CouponType.cart_wise)) {
                discount = calculateCartWiseDiscount(coupon, totalPrice);
            } else if (coupon.getType().equals(CouponType.product_wise)) {
                discount = calculateProductWiseDiscount(coupon, cartDTO.getItems());
            } else if (coupon.getType().equals(CouponType.bxgy)) {
                discount = calculateBxGyDiscount(coupon, cartDTO.getItems());
            }
            if (discount > 0) {
                ApplicableCouponResponseDTO response = new ApplicableCouponResponseDTO(coupon.getId(), coupon.getType().name(), discount);
                applicableCoupons.add(response);
            }
        }

        return applicableCoupons;
    }

    public CartDTO applyCoupon(Long couponId, CartDTO cartDTO) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new IllegalArgumentException("Invalid coupon ID"));
        double totalPrice = calculateCartTotal(cartDTO);
        double discount = 0;

        if (coupon.getType().equals(CouponType.cart_wise)) {
            discount = calculateCartWiseDiscount(coupon, totalPrice);
        } else if (coupon.getType().equals(CouponType.product_wise)) {
            discount = applyProductWiseCoupon(coupon, cartDTO);
        } else if (coupon.getType().equals(CouponType.bxgy)) {
            discount = applyBxGyCoupon(coupon, cartDTO);
        }

        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setTotalDiscount(discount);
        cartDTO.setFinalPrice(totalPrice - discount);
        return cartDTO;
    }

    private double calculateCartTotal(CartDTO cartDTO) {
        return cartDTO.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private double calculateCartWiseDiscount(Coupon coupon, double totalPrice) {
        Map<String, Object> discountDetails = coupon.getDiscountDetails();

        //double percentageDiscount = (double) discountDetails.get("discount");
        Object discountObj = discountDetails.get("discount");

        double percentageDiscount;

        if (discountObj instanceof Integer) {
            percentageDiscount = ((Integer) discountObj).doubleValue(); // Convert Integer to double
        } else if (discountObj instanceof Double) {
            percentageDiscount = (Double) discountObj; // It's already a double
        } else {
            throw new IllegalArgumentException("Invalid discount type");
        }

        totalPrice = totalPrice * (percentageDiscount / 100);

        return Math.round(totalPrice / 10.0) * 10.0;
    }

    private double calculateProductWiseDiscount(Coupon coupon, List<CartItemDTO> cartItems) {
        Map<String, Object> discountDetails = coupon.getDiscountDetails();
        Long productId = ((Number) discountDetails.get("productId")).longValue();
        //double discountAmount = (double) discountDetails.get("discountAmount");
        Object discountObj = discountDetails.get("discount");

        double discountAmount = 0.0;

        discountAmount = getDiscountAmount(discountObj, discountAmount);

        for (CartItemDTO item : cartItems) {
            if (item.getProductId().equals(productId)) {
                return discountAmount; // Apply discount on the specific product
            }
        }
        return 0;
    }

    private double applyProductWiseCoupon(Coupon coupon, CartDTO cartDTO) {
        Map<String, Object> discountDetails = coupon.getDiscountDetails();
        Long productId = ((Number) discountDetails.get("productId")).longValue();
        //double discountAmount = (double) discountDetails.get("discount");
        Object discountObj = discountDetails.get("discount");

        double discountAmount = 0.0; // Default value, in case it's null

        discountAmount = getDiscountAmount(discountObj, discountAmount);

        for (CartItemDTO item : cartDTO.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setTotalDiscount(discountAmount);
                return discountAmount;
            }
        }
        return 0;
    }

    private double getDiscountAmount(Object discountObj, double discountAmount) {
        if (discountObj != null) {
            if (discountObj instanceof Integer) {
                discountAmount = ((Integer) discountObj).doubleValue(); // Convert Integer to double
            } else if (discountObj instanceof Double) {
                discountAmount = (Double) discountObj; // It's already a double
            } else {
                throw new IllegalArgumentException("Invalid discountAmount type");
            }
        } else {
            System.out.println("discountAmount is null, using default value.");
        }
        return discountAmount;
    }


    private double calculateBxGyDiscount(Coupon coupon, List<CartItemDTO> cartItems) {

        List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) coupon.getDiscountDetails().get("buyProducts");
        List<Map<String, Object>> getProducts = (List<Map<String, Object>>) coupon.getDiscountDetails().get("getProducts");
        Integer repetitionLimitValue = (Integer) coupon.getDiscountDetails().get("repetitionLimit");
        int repetitionLimit = (repetitionLimitValue != null) ? repetitionLimitValue : 0;

        int totalBuyItems = 0;
        int totalFreeItems = 0;
        double totalDiscount = 0;

        List<Map<String, Object>> safeBuyProducts = Optional.ofNullable(buyProducts).orElse(Collections.emptyList());

        for (CartItemDTO cartItem : cartItems) {
            for (Map<String, Object> buyProduct : safeBuyProducts) {
                if (cartItem.getProductId() == (int) buyProduct.get("productId")) {
                    int requiredQuantity = (int) buyProduct.get("quantity");

                    totalBuyItems += cartItem.getQuantity() / requiredQuantity;
                }
            }
        }

        totalBuyItems = Math.min(totalBuyItems, repetitionLimit);

        List<Map<String, Object>> safeGetProducts = Optional.ofNullable(getProducts).orElse(Collections.emptyList());

        for (CartItemDTO cartItem : cartItems) {
            for (Map<String, Object> getProduct : safeGetProducts) {
                if (cartItem.getProductId() == (int) getProduct.get("productId")) {
                    int freeQuantity = (int) getProduct.get("quantity");
                    totalFreeItems += Math.min(cartItem.getQuantity(), totalBuyItems * freeQuantity);

                    totalDiscount += totalFreeItems * cartItem.getPrice();
                }
            }
        }

        return totalDiscount;
    }

    private double applyBxGyCoupon(Coupon coupon, CartDTO cartDTO) {

        return 0;
    }
}
