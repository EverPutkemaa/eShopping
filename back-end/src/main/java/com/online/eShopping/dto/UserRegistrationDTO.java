package com.online.eShopping.dto;

import jakarta.validation.constraints.Email;
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
public class UserRegistrationDTO {

    @NotBlank(message = "Email on kohustuslik")
    @Email(message = "Palun sisesta korrektne email")
    private String email;

    @NotBlank(message = "Parool on kohustuslik")
    @Size(min = 8, message = "Parool peab olema vähemalt 8 tähemärki")
    private String password;

    private String firstName;

    private String lastName;
}
