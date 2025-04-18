package com.online.eShopping.resource;

import com.online.eShopping.dto.CartDTO;
import com.online.eShopping.dto.CartItemCreateDTO;
import com.online.eShopping.dto.OrderDTO;
import com.online.eShopping.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/me")
    public ResponseEntity<CartDTO> getMyCart() {
        return ResponseEntity.ok(cartService.getCurrentUserCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody CartItemCreateDTO cartItemDTO) {
        return ResponseEntity.ok(cartService.addItemToCart(cartItemDTO));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(productId, quantity));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart() {
        return ResponseEntity.ok(cartService.clearCart());
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkoutCart(
            @RequestParam(required = false) Long shippingAddressId,
            @RequestParam String shippingMethod,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(cartService.checkoutCart(shippingAddressId, shippingMethod, paymentMethod));
    }
}
