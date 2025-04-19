package com.online.eShopping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateDTO {

    @NotNull(message = "Toote ID on kohustuslik")
    private Long productId;

    @NotNull(message = "Kogus on kohustuslik")
    @Min(value = 1, message = "Kogus peab olema vähemalt 1")
    private Integer quantity;
}

