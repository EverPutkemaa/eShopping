package com.online.eShopping.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Toote nimi on kohustuslik")
    @Size(min = 2, max = 100, message = "Toote nimi peab olema 2-100 tähemärki")
    private String name;

    @Size(max = 2000, message = "Kirjeldus ei tohi olla pikem kui 2000 tähemärki")
    private String description;

    @NotNull(message = "Hind on kohustuslik")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hind peab olema suurem kui 0")
    @Digits(integer = 8, fraction = 2, message = "Hind peab olema korrektses vormingus")
    private BigDecimal price;

    @NotNull(message = "Laoseis on kohustuslik")
    @Min(value = 0, message = "Laoseis ei saa olla negatiivne")
    private Integer stockQuantity;

    private String imageUrl;

    @NotNull(message = "Kategooria ID on kohustuslik")
    private Long categoryId;
}
