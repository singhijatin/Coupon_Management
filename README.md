# Coupon_Management

## Objective

This project aims to create a RESTful API to manage and apply different types of discount coupons (cart-wise, product-wise, and BxGy) for an e-commerce platform. The focus is on flexibility for adding new coupon types in the future and ensuring a variety of cases are considered.

## Project Structure

- **API Endpoints**: Provides CRUD operations for coupons and applies coupon logic to a cart.
- **Coupon Types**:
  - **Cart-wise Discounts**: Apply discounts to the entire cart if it exceeds a threshold.
  - **Product-wise Discounts**: Apply discounts to specific products.
  - **BxGy Deals**: Buy X items and get Y items free, with a repetition limit.

## Dependencies Added

- Spring Web
- Spring Data JPA
- MySQL Driver
- Lombok

## Key Endpoints

- `POST /coupons`: Create a new coupon.
- `GET /coupons`: Retrieve all coupons.
- `GET /coupons/{id}`: Retrieve a coupon by ID.
- `PUT /coupons/{id}`: Update a coupon by ID.
- `DELETE /coupons/{id}`: Delete a coupon by ID.
- `POST /applicable-coupons`: Fetch applicable coupons for a cart and calculate the total discount.
- `POST /apply-coupon/{id}`: Apply a specific coupon to a cart.

## Payload for cart-wise -
{
    "type": "cart_wise",
    "discountDetails": {
        "threshold": 100,
        "discount": 10
    },
    "expiryDate": "2024-10-31",
    "isActive": true
}

## Payload for product-wise -
{
    "type": "product_wise",
    "discountDetails": {
        "productId": 1,
        "discount": 20
    },
    "expiryDate": "2025-09-30",
    "isActive": true
}

## Payload for BxGy -
{
    "type": "bxgy",
    "discountDetails": {
        "buyProducts": [
            {
                "productId": 1,
                "quantity": 3
            },
            {
                "productId": 2,
                "quantity": 3
            }
        ],
        "getProducts": [
            {
                "productId": 3,
                "quantity": 1
            }
        ],
        "repetitionLimit": 2
    },
    "expiryDate": "2024-11-30",
    "isActive": true
}

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
3. **BxGy Coupons**:
   - **Example**: Buy 2 products from the "buy" array (X, Y) and get 1 product from the "get" array (A, B) free.
   - **Condition**: The cart must have sufficient products from the "buy" array.
   - **Discount**: Y items from the "get" array are free.
   - **Implementation**: Applies the discount with a repetition limit.

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
