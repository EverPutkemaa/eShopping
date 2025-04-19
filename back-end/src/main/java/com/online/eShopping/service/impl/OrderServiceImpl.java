package com.online.eShopping.service.impl;

import com.online.eShopping.dto.OrderCreateDTO;
import com.online.eShopping.dto.OrderDTO;
import com.online.eShopping.dto.OrderItemCreateDTO;
import com.online.eShopping.dto.OrderStatusUpdateDTO;
import com.online.eShopping.exception.InsufficientStockException;
import com.online.eShopping.exception.InvalidOperationException;
import com.online.eShopping.exception.ResourceNotFoundException;
import com.online.eShopping.mapper.OrderMapper;
import com.online.eShopping.model.Order;
import com.online.eShopping.model.OrderItem;
import com.online.eShopping.model.Product;
import com.online.eShopping.model.User;
import com.online.eShopping.repository.OrderRepository;
import com.online.eShopping.repository.ProductRepository;
import com.online.eShopping.repository.UserRepository;
import com.online.eShopping.service.OrderService;
import com.online.eShopping.service.ProductService;
import com.online.eShopping.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final UserService userService;


    @Override
    @Transactional
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        log.info("Creating new order");

        // Leia praegune kasutaja
        User user = getCurrentUser();

        // Loo uus tellimus
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setShippingAddress(orderMapper.toAddressEntity(orderCreateDTO.getShippingAddress()));
        order.setShippingMethod(orderCreateDTO.getShippingMethod());
        order.setPaymentMethod(orderCreateDTO.getPaymentMethod());
        order.setPaymentStatus("AWAITING_PAYMENT");

        // Lisa tellimuse ühikud
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCreateDTO itemDTO : orderCreateDTO.getItems()) {
            // Leia toode
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            // Kontrolli laoseisu
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            // Loo tellimuse ühik
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            // Uuenda kogu hinda
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            // Vähenda laoseisu
            productService.reduceStock(product.getId(), itemDTO.getQuantity());
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Salvesta tellimus
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());

        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Kontrolli, kas kasutajal on õigus seda tellimust vaadata
        User currentUser = getCurrentUser();
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")) &&
                !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }

        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO) {
        log.info("Updating order status for id: {} to {}", id, statusUpdateDTO.getStatus());

        // Ainult administraator võib tellimuse staatust muuta
        User currentUser = getCurrentUser();
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can update order status");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Uuenda staatust
        order.setStatus(statusUpdateDTO.getStatus());
        order.setUpdatedAt(LocalDateTime.now());

        // Kui staatus on "DELIVERED", uuenda makse staatust "PAID"
        if (statusUpdateDTO.getStatus().equals("DELIVERED")) {
            order.setPaymentStatus("PAID");
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated for id: {}", id);

        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(int page, int size) {
        log.info("Fetching all orders page {} with size {}", page, size);

        // Ainult administraator võib kõiki tellimusi vaadata
        User currentUser = getCurrentUser();
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can view all orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size) {
        log.info("Fetching orders for user id: {}", userId);

        // Kontrolli, kas kasutaja eksisteerib
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Ainult administraator või sama kasutaja võib tellimusi vaadata
        User currentUser = getCurrentUser();
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")) &&
                !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to view these orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);

        return orderPage.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getCurrentUserOrders(int page, int size) {
        User currentUser = getCurrentUser();
        log.info("Fetching orders for current user id: {}", currentUser.getId());

        return getUserOrders(currentUser.getId(), page, size);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Kontrolli, kas kasutajal on õigus tellimust tühistada
        User currentUser = getCurrentUser();
        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")) &&
                !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to cancel this order");
        }

        // Kontrolli, kas tellimust saab tühistada
        if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("PROCESSING")) {
            throw new InvalidOperationException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Uuenda staatust
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());

        // Taasta laoseis
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order cancelled with id: {}", id);

        return orderMapper.toDTO(cancelledOrder);
    }

    /**
     * Abimeetod praeguse kasutaja leidmiseks.
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
    }
}