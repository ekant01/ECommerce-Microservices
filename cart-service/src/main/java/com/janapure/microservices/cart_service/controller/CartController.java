package com.janapure.microservices.cart_service.controller;

import com.janapure.microservices.cart_service.dto.AddCartItemRequest;
import com.janapure.microservices.cart_service.dto.CartDto;
import com.janapure.microservices.cart_service.dto.CheckOutRequest;
import com.janapure.microservices.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add methods to handle HTTP requests here
    @PostMapping("/cart/add")
    public ResponseEntity<CartDto> addToCart(@RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        //cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/cart/{userId}/remove/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cart/{userId}/items/{productId}/increase")
    public ResponseEntity<CartDto> increaseItemQuantity(
            @PathVariable String userId,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.increaseItemQuantity(userId, productId));
    }

    @PostMapping("/cart/{userId}/items/{productId}/decrease")
    public ResponseEntity<CartDto> decreaseItemQuantity(
            @PathVariable String userId,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.decreaseItemQuantity(userId, productId));
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<String> checkout(@RequestBody CheckOutRequest request) {
        cartService.checkoutCart(request);
        return ResponseEntity.ok("Checkout initiated for user: " + request.getUserId());
    }

    @GetMapping("/cart/health/{userId}")
    public ResponseEntity<Boolean> healthCheck(@PathVariable String userId) {

        return ResponseEntity.ok(cartService.healthCheck(userId));
    }

    @PostMapping("/cart/checkout/dummy")
    public ResponseEntity<String> checkoutDummy(@RequestBody CheckOutRequest request) {
        cartService.checkoutCartDummy(request);
        return ResponseEntity.ok("Checkout initiated for user: " + request.getUserId());
    }
}
