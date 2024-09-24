package com.example.CouponSystem;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.CouponSystem.controller.CouponController;
import com.example.CouponSystem.model.ApplicableCouponResponseDTO;
import com.example.CouponSystem.model.CartDTO;
import com.example.CouponSystem.model.Coupon;
import com.example.CouponSystem.model.CouponType;
import com.example.CouponSystem.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

class CouponControllerTest {

    @InjectMocks
    private CouponController couponController;

    @Mock
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon() {
        Coupon coupon = getCoupon();
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(coupon);

        ResponseEntity<Coupon> response = couponController.createCoupon(coupon);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coupon, response.getBody());
        verify(couponService).createCoupon(any(Coupon.class));
    }

    private Coupon getCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType(CouponType.cart_wise);
        Map<String, Object> discountDetails = new HashMap<>();
        coupon.setDiscountDetails(discountDetails);
        coupon.setExpiryDate(LocalDate.of(2024, 12, 31));
        coupon.setIsActive(true);
        return coupon;
    }

    @Test
    void testGetAllCoupons() {
        Coupon coupon1 = getCoupon();
        Coupon coupon2 = getCoupon();
        List<Coupon> coupons = Arrays.asList(coupon1,coupon2);
        when(couponService.getAllCoupons()).thenReturn(coupons);

        List<Coupon> responseCoupons = couponController.getAllCoupons();

        assertEquals(2, responseCoupons.size());
        assertEquals(coupons, responseCoupons);
        verify(couponService).getAllCoupons();
    }

    @Test
    void testGetCouponById() {
        Coupon coupon = getCoupon();
        when(couponService.getCouponById(1L)).thenReturn(coupon);

        ResponseEntity<Coupon> response = couponController.getCouponById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coupon, response.getBody());
        verify(couponService).getCouponById(1L);
    }

    @Test
    void testUpdateCoupon() {
        Coupon updatedCoupon = getCoupon();
        updatedCoupon.setId(2L);
        when(couponService.updateCoupon(anyLong(), any(Coupon.class))).thenReturn(updatedCoupon);

        ResponseEntity<Coupon> response = couponController.updateCoupon(1L, updatedCoupon);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCoupon, response.getBody());
        verify(couponService).updateCoupon(anyLong(), any(Coupon.class));
    }

    @Test
    void testDeleteCoupon() {
        doNothing().when(couponService).deleteCoupon(1L);

        ResponseEntity<Void> response = couponController.deleteCoupon(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(couponService).deleteCoupon(1L);
    }

    @Test
    void testGetApplicableCoupons() {
        CartDTO cart = mock(CartDTO.class);
        List<ApplicableCouponResponseDTO> applicableCoupons = Arrays.asList(
                new ApplicableCouponResponseDTO(1L, "cart-wise", 40),
                new ApplicableCouponResponseDTO(2L, "bxgy", 50)
        );
        when(couponService.getApplicableCoupons(cart)).thenReturn(applicableCoupons);

        ResponseEntity<List<ApplicableCouponResponseDTO>> response = couponController.getApplicableCoupons(cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicableCoupons, response.getBody());
        verify(couponService).getApplicableCoupons(cart);
    }

    @Test
    void testApplyCoupon() {
        CartDTO cart = mock(CartDTO.class);
        CartDTO updatedCart = mock(CartDTO.class);
        when(couponService.applyCoupon(1L, cart)).thenReturn(updatedCart);

        ResponseEntity<CartDTO> response = couponController.applyCoupon(1L, cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCart, response.getBody());
        verify(couponService).applyCoupon(1L, cart);
    }
}

