package com.online.eShopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    @NotBlank(message = "Kategooria nimi on kohustuslik")
    @Size(min = 2, max = 50, message = "Kategooria nimi peab olema 2-50 tähemärki")
    private String name;

    private String description;

    private Long parentId;

}
