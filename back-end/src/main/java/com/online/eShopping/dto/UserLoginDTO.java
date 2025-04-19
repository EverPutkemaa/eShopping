package com.online.eShopping.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @NotBlank(message = "Email on kohustuslik")
    @Email(message = "Palun sisesta korrektne email")
    private String email;

    @NotBlank(message = "Parool on kohustuslik")
    private String password;
}
