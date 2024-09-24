# Coupon_Management

## Objective

This project aims to create a RESTful API to manage and apply different types of discount coupons (cart-wise, product-wise, and BxGy) for an e-commerce platform. The focus is on flexibility for adding new coupon types in the future and ensuring a variety of cases are considered.

## Project Structure

- **API Endpoints**: Provides CRUD operations for coupons and applies coupon logic to a cart.
- **Coupon Types**:
  - **Cart-wise Discounts**: Apply discounts to the entire cart if it exceeds a threshold.
  - **Product-wise Discounts**: Apply discounts to specific products.
  - **BxGy Deals**: Buy X items and get Y items free, with a repetition limit.

## Key Endpoints

- `POST /coupons`: Create a new coupon.
- `GET /coupons`: Retrieve all coupons.
- `GET /coupons/{id}`: Retrieve a coupon by ID.
- `PUT /coupons/{id}`: Update a coupon by ID.
- `DELETE /coupons/{id}`: Delete a coupon by ID.
- `POST /applicable-coupons`: Fetch applicable coupons for a cart and calculate the total discount.
- `POST /apply-coupon/{id}`: Apply a specific coupon to a cart.

## Implemented Cases

1. **Cart-wise Coupons**:

   - **Example**: 10% off on carts over ₹100.
   - **Condition**: Cart total > ₹100.
   - **Discount**: 10% off the entire cart.
   - **Implementation**: Validates if the cart total meets the threshold, then applies the discount.

2. **Product-wise Coupons**:
   - **Example**: 20% off on Product A.
   - **Condition**: Product A must be in the cart.
   - **Discount**: 20% off Product A.
   - **Implementation**: Checks if the specific product is in the cart and applies the discount.

## Limitations

1. **Performance Scaling**: The current design works for small-scale operations, but performance optimizations would be required for larger-scale systems.
2. **Coupon Expiration Date**: Not implemented the logic yet but added in Coupon model class.
3. **Coupon is Active**: Not implemented the logic yet but added in Coupon model class.

## Future Enhancements

- Adding **user-specific coupons** and integrating with user accounts.
- Implementing **coupon expiration** and limits based on usage.
- Optimizing for **larger datasets**.
- Adding **Free shiping coupon** if the overall cart value exceeds the threshold.

## Bonus Features Added

- Implemented **unit tests** to validate the logic.
- Added basic **error handling** for invalid inputs.
