package com.online.eShopping.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    @NotEmpty(message = "Tellimuse ühikud on kohustuslikud")
    private List<@Valid OrderItemCreateDTO> items;

    @NotNull(message = "Tarneaadress on kohustuslik")
    @Valid
    private AddressDTO shippingAddress;

    @NotBlank(message = "Tarnemeetod on kohustuslik")
    private String shippingMethod;

    @NotBlank(message = "Maksemeetod on kohustuslik")
    private String paymentMethod;
}
