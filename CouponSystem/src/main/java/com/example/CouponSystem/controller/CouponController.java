package com.example.CouponSystem.controller;

import com.example.CouponSystem.model.ApplicableCouponResponseDTO;
import com.example.CouponSystem.model.CartDTO;
import com.example.CouponSystem.model.Coupon;
import com.example.CouponSystem.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon couponDetails) {
        Coupon updatedCoupon = couponService.updateCoupon(id, couponDetails);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<ApplicableCouponResponseDTO>> getApplicableCoupons(@RequestBody CartDTO cartDTO) {
        List<ApplicableCouponResponseDTO> applicableCoupons = couponService.getApplicableCoupons(cartDTO);
        return ResponseEntity.ok(applicableCoupons);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartDTO> applyCoupon(@PathVariable Long id, @RequestBody CartDTO cartDTO) {
        CartDTO updatedCart = couponService.applyCoupon(id, cartDTO);
        return ResponseEntity.ok(updatedCart);
    }
}