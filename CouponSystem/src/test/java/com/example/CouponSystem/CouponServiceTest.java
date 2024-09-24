package com.example.CouponSystem;

import com.example.CouponSystem.exception.ResourceNotFoundException;
import com.example.CouponSystem.model.*;
import com.example.CouponSystem.repo.CouponRepository;
import com.example.CouponSystem.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType(CouponType.cart_wise);

        when(couponRepository.save(coupon)).thenReturn(coupon);

        Coupon createdCoupon = couponService.createCoupon(coupon);

        assertNotNull(createdCoupon);
        assertEquals(1L, createdCoupon.getId());
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testGetAllCoupons() {
        List<Coupon> coupons = Arrays.asList(new Coupon(), new Coupon());

        when(couponRepository.findAll()).thenReturn(coupons);

        List<Coupon> result = couponService.getAllCoupons();

        assertEquals(2, result.size());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testGetCouponById() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        Coupon result = couponService.getCouponById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCouponById_NotFound() {
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> couponService.getCouponById(1L));
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateCoupon() {
        Coupon existingCoupon = new Coupon();
        existingCoupon.setId(1L);
        existingCoupon.setType(CouponType.cart_wise);

        Coupon couponDetails = new Coupon();
        couponDetails.setType(CouponType.product_wise);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(existingCoupon));
        when(couponRepository.save(existingCoupon)).thenReturn(existingCoupon);

        Coupon updatedCoupon = couponService.updateCoupon(1L, couponDetails);

        assertEquals(CouponType.product_wise, updatedCoupon.getType());
        verify(couponRepository, times(1)).save(existingCoupon);
    }

    @Test
    void testDeleteCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        couponService.deleteCoupon(1L);

        verify(couponRepository, times(1)).delete(coupon);
    }

    @Test
    void testGetApplicableCoupons() {
        CartItemDTO item1 = new CartItemDTO(1L, 3, 100.0,0);
        CartItemDTO item2 = new CartItemDTO(2L, 2, 200.0,0);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Arrays.asList(item1,item2));

        Coupon coupon1 = new Coupon();
        coupon1.setId(1L);
        coupon1.setType(CouponType.cart_wise);
        coupon1.setDiscountDetails(Map.of("discount", 10)); // 10% off

        Coupon coupon2 = new Coupon();
        coupon2.setId(2L);
        coupon2.setType(CouponType.product_wise);
        coupon2.setDiscountDetails(Map.of("productId", 1, "discount", 50));

        List<Coupon> coupons = Arrays.asList(coupon1, coupon2);
        when(couponRepository.findAll()).thenReturn(coupons);

        List<ApplicableCouponResponseDTO> result = couponService.getApplicableCoupons(cartDTO);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testApplyCoupon_CartWise() {
        CartItemDTO item1 = new CartItemDTO(1L, 3, 100.0,0);
        CartItemDTO item2 = new CartItemDTO(2L, 2, 200.0,0);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Arrays.asList(item1,item2));

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType(CouponType.cart_wise);
        coupon.setDiscountDetails(Map.of("discount", 10)); // 10% off

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        CartDTO updatedCart = couponService.applyCoupon(1L, cartDTO);

        assertEquals(700.0, updatedCart.getTotalPrice());
        assertEquals(70, updatedCart.getTotalDiscount());
        assertEquals(630, updatedCart.getFinalPrice());
    }

    @Test
    void testApplyCoupon_ProductWise() {
        CartItemDTO item1 = new CartItemDTO(1L, 3, 100.0,0);
        CartItemDTO item2 = new CartItemDTO(2L, 2, 200.0,0);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(Arrays.asList(item1,item2));

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType(CouponType.product_wise);
        coupon.setDiscountDetails(Map.of("productId", 1, "discount", 50)); // $50 off product 1

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        CartDTO updatedCart = couponService.applyCoupon(1L, cartDTO);

        assertEquals(700.0, updatedCart.getTotalPrice());
        assertEquals(50.0, updatedCart.getTotalDiscount());
        assertEquals(650.0, updatedCart.getFinalPrice());
    }
}
