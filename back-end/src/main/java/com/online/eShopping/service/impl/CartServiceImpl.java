package com.online.eShopping.service.impl;

import com.online.eShopping.dto.*;
import com.online.eShopping.exception.InsufficientStockException;
import com.online.eShopping.exception.ResourceNotFoundException;
import com.online.eShopping.mapper.CartMapper;
import com.online.eShopping.model.Cart;
import com.online.eShopping.model.CartItem;
import com.online.eShopping.model.Product;
import com.online.eShopping.model.User;
import com.online.eShopping.repository.CartItemRepository;
import com.online.eShopping.repository.CartRepository;
import com.online.eShopping.repository.ProductRepository;
import com.online.eShopping.repository.UserRepository;
import com.online.eShopping.service.CartService;
import com.online.eShopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCurrentUserCart() {
        log.info("Fetching cart for current user");

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(CartItemCreateDTO cartItemDTO) {
        log.info("Adding item to cart: productId={}, quantity={}", cartItemDTO.getProductId(), cartItemDTO.getQuantity());

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Leia toode
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItemDTO.getProductId()));

        // Kontrolli laoseisu
        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Kontrolli, kas toode on juba ostukorvis
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemDTO.getProductId()))
                .findFirst();

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            // Uuenda olemasolevat ühikut
            cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + cartItemDTO.getQuantity();

            // Kontrolli uuesti laoseisu
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            cartItem.setQuantity(newQuantity);
        } else {
            // Loo uus ühik
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cart.getItems().add(cartItem);
        }

        // Uuenda ostukorvi
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        log.info("Item added to cart for user: {}", user.getEmail());
        return cartMapper.toDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO updateItemQuantity(Long productId, Integer quantity) {
        log.info("Updating item quantity in cart: productId={}, quantity={}", productId, quantity);

        if (quantity <= 0) {
            return removeItemFromCart(productId);
        }

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Leia toode ostukorvist
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with id: " + productId));

        // Kontrolli laoseisu
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        // Uuenda kogust
        cartItem.setQuantity(quantity);

        // Uuenda ostukorvi
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        log.info("Item quantity updated in cart for user: {}", user.getEmail());
        return cartMapper.toDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long productId) {
        log.info("Removing item from cart: productId={}", productId);

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Leia toode ostukorvist
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with id: " + productId));

        // Eemalda ühik ostukorvist
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        // Uuenda ostukorvi
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        log.info("Item removed from cart for user: {}", user.getEmail());
        return cartMapper.toDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO clearCart() {
        log.info("Clearing cart");

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Eemalda kõik ühikud
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        // Uuenda ostukorvi
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        log.info("Cart cleared for user: {}", user.getEmail());
        return cartMapper.toDTO(updatedCart);
    }

    @Override
    @Transactional
    public OrderDTO checkoutCart(Long shippingAddressId, String shippingMethod, String paymentMethod) {
        log.info("Checking out cart with shippingAddressId={}, shippingMethod={}, paymentMethod={}",
                shippingAddressId, shippingMethod, paymentMethod);

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        // Kontrolli, kas ostukorv on tühi
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }

        // Loo tellimuse andmed
        List<OrderItemCreateDTO> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            OrderItemCreateDTO orderItemDTO = new OrderItemCreateDTO();
            orderItemDTO.setProductId(item.getProduct().getId());
            orderItemDTO.setQuantity(item.getQuantity());
            orderItems.add(orderItemDTO);
        }

        // TODO: Implement address repository and fetching
        // For now, we'll create a new AddressDTO
        AddressDTO shippingAddress = new AddressDTO();
        shippingAddress.setStreetAddress("Example Street 123");
        shippingAddress.setCity("Example City");
        shippingAddress.setState("Example State");
        shippingAddress.setPostalCode("12345");
        shippingAddress.setCountry("Example Country");
        shippingAddress.setPhoneNumber("+37255512345");

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setItems(orderItems);
        orderCreateDTO.setShippingAddress(shippingAddress);
        orderCreateDTO.setShippingMethod(shippingMethod);
        orderCreateDTO.setPaymentMethod(paymentMethod);

        // Loo tellimus
        OrderDTO order = orderService.createOrder(orderCreateDTO);

        // Tühjenda ostukorv
        clearCart();

        log.info("Cart checkout completed for user: {}, created order with id: {}", user.getEmail(), order.getId());
        return order;
    }

    /**
     * Abimeetod praeguse kasutaja leidmiseks.
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
    }

    /**
     * Abimeetod kasutaja ostukorvi leidmiseks või loomiseks, kui seda ei ole.
     */
    private Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart = cartRepository.save(cart);
        }

        return cart;
    }
}
