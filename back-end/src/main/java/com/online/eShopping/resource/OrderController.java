package com.online.eShopping.resource;

import com.online.eShopping.dto.OrderCreateDTO;
import com.online.eShopping.dto.OrderDTO;
import com.online.eShopping.dto.OrderStatusUpdateDTO;
import com.online.eShopping.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<OrderDTO>> getCurrentUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getCurrentUserOrders(page, size));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusUpdateDTO));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
