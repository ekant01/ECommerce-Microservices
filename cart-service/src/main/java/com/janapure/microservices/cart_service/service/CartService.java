package com.janapure.microservices.cart_service.service;

import com.janapure.microservices.cart_service.dto.*;
import com.janapure.microservices.cart_service.entities.Cart;
import com.janapure.microservices.cart_service.entities.CartItem;
import com.janapure.microservices.cart_service.repository.CartItemRepo;
import com.janapure.microservices.cart_service.repository.CartRepo;
import com.janapure.microservices.proto.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private CartRepo cartRepo;

    private CartItemRepo cartItemRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @Autowired
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo) {
        System.out.println("CartService initialized");
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public CartDto addToCart(AddCartItemRequest request) {
        // Check if the user is valid
        UserIdRequest userIdRequest = UserIdRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();
        boolean isValidUser = userServiceBlockingStub.validateUser(userIdRequest).getIsValid();
        if (!isValidUser) {
            throw new RuntimeException("Invalid user ID: " + request.getUserId());
        }
        // Check if the product is valid
        ProductResponse productResponse = productServiceBlockingStub
                .getProductInfo(ProductRequest.newBuilder().setProductId(request.getProductId()).build());
        if (productResponse == null) {
            throw new RuntimeException("Invalid product ID: " + request.getProductId());
        } else {
            // Check if the product is in stock
            if (productResponse.getPrice() != request.getPrice()) {
                throw new RuntimeException("Price mismatch for product: " + request.getProductId());
            }
            CheckStockRequest checkStockRequest = CheckStockRequest.newBuilder()
                    .setProductId(request.getProductId())
                    .setQuantity(request.getQuantity())
                    .build();
            CheckStockResponse checkStockResponse = productServiceBlockingStub.checkStock(checkStockRequest);
            if (!checkStockResponse.getIsAvailable()) {
                throw new RuntimeException("Product is out of stock");
            }

        }
        Cart cart = cartRepo.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(request.getUserId());
                    newCart.setCreatedAt(LocalDateTime.now());
                    return newCart;
                });

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(request.getProductId());
        item.setProductName(request.getProductName());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setAddedAt(LocalDateTime.now());

        cart.setUpdatedAt(LocalDateTime.now());
        cart.getCartItems().add(item);

        cartRepo.save(cart);
        return mapToDTO(cart);

    }

    public CartDto getCartByUserId(String userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        return mapToDTO(cart);
    }

    private CartDto mapToDTO(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        List<CartItemDto> itemDTOs = cart.getCartItems().stream().map(item -> {
            CartItemDto itemDTO = new CartItemDto();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProductId());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setAddedAt(item.getAddedAt());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setCartItems(itemDTOs);
        return dto;
    }

    public void checkoutCart(CheckOutRequest request) {
        Cart cart = cartRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

//        Cart cart = new Cart();
//        cart.setUserId("abc@Gmai.com");
//        cart.setCreatedAt(LocalDateTime.now());
//        cart.setUpdatedAt(LocalDateTime.now());
//        cart.setCartItems(List.of(new CartItem()));
//        cart.getCartItems().get(0).setProductId("123");
//        cart.getCartItems().get(0).setProductName("Product 1");
//        cart.getCartItems().get(0).setQuantity(2);
//        cart.getCartItems().get(0).setPrice(100.0);
//        cart.getCartItems().get(0).setAddedAt(LocalDateTime.now());
//        cart.getCartItems().get(0).setCart(cart);

        OrderCreateEvent event = new OrderCreateEvent();
        event.setUserId(request.getUserId());
        event.setPaymentMode(request.getPaymentMode());
        List<OrderCreateEvent.OrderItem> items = cart.getCartItems().stream().map(ci -> {
            OrderCreateEvent.OrderItem item = new OrderCreateEvent.OrderItem();
            item.setProductId(ci.getProductId());
            item.setProductName(ci.getProductName());
            item.setQuantity(ci.getQuantity());
            item.setPrice(ci.getPrice());
            return item;
        }).toList();

        event.setItems(items);

        kafkaTemplate.send("checkout.request", event);
    }

    public void removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        List<CartItem> itemsToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .collect(Collectors.toList());

        if (!itemsToRemove.isEmpty()) {
            cart.getCartItems().removeAll(itemsToRemove);
            cartRepo.save(cart);
        }
    }

    public CartDto increaseItemQuantity(String userId, String productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(item.getQuantity() + 1);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return mapToDTO(cart);
    }

    public CartDto decreaseItemQuantity(String userId, String productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cart.getCartItems().remove(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return mapToDTO(cart);
    }

    public Boolean healthCheck(String userId) {
        UserIdRequest request = UserIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        ProductRequest productRequest = ProductRequest.newBuilder()
                .setProductId(userId)
                .build();
        //ProductResponse productResponse = productServiceBlockingStub.getProductInfo(productRequest);

        try {
            ProductResponse response = productServiceBlockingStub.getProductInfo(productRequest);
            // process response
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                // handle 404-like logic
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getStatus().getDescription());
            } else {
                // log and rethrow for other issues
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "gRPC error: " + e.getMessage());
            }
        }
        
        return userServiceBlockingStub.validateUser(request).getIsValid();
    }

    public void checkoutCartDummy(CheckOutRequest request) {
//        Cart cart = cartRepo.findByUserId(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Cart cart = new Cart();
        cart.setUserId("abc@Gmai.com");
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setCartItems(List.of(new CartItem()));
        cart.getCartItems().get(0).setProductId("123");
        cart.getCartItems().get(0).setProductName("Product 1");
        cart.getCartItems().get(0).setQuantity(2);
        cart.getCartItems().get(0).setPrice(100.0);
        cart.getCartItems().get(0).setAddedAt(LocalDateTime.now());
        cart.getCartItems().get(0).setCart(cart);
        OrderCreateEvent event = new OrderCreateEvent();
        event.setUserId(request.getUserId());
        event.setPaymentMode(request.getPaymentMode());
        List<OrderCreateEvent.OrderItem> items = cart.getCartItems().stream().map(ci -> {
            OrderCreateEvent.OrderItem item = new OrderCreateEvent.OrderItem();
            item.setProductId(ci.getProductId());
            item.setProductName(ci.getProductName());
            item.setQuantity(ci.getQuantity());
            item.setPrice(ci.getPrice());
            return item;
        }).toList();

        event.setItems(items);

        kafkaTemplate.send("checkout.request", event);
    }

    @KafkaListener(topics = "cart.clear", groupId = "cart-service")
    public void clearCart(String userId) {
        System.out.println("Received request to clear cart for user: " + userId);
        Optional<Cart> cart = cartRepo.findByUserId(userId);
        if (cart.isEmpty()) {
            System.out.println("No cart found for user: " + userId);
            return;
        }
        Cart existingCart = cart.get();
        existingCart.getCartItems().clear();
        existingCart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(existingCart);
    }
}
