package com.online.eShopping.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String firstName;

    private String lastName;

    @Size(min = 8, message = "Parool peab olema vähemalt 8 tähemärki")
    private String password;
}
