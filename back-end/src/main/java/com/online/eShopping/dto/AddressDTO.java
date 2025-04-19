package com.online.eShopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @NotBlank(message = "Tänava aadress on kohustuslik")
    @Size(max = 100, message = "Tänava aadress peab olema kuni 100 tähemärki")
    private String streetAddress;

    @NotBlank(message = "Linn on kohustuslik")
    @Size(max = 50, message = "Linna nimi peab olema kuni 50 tähemärki")
    private String city;

    @Size(max = 50, message = "Maakond/osariik peab olema kuni 50 tähemärki")
    private String state;

    @NotBlank(message = "Postiindeks on kohustuslik")
    @Size(max = 20, message = "Postiindeks peab olema kuni 20 tähemärki")
    private String postalCode;

    @NotBlank(message = "Riik on kohustuslik")
    @Size(max = 50, message = "Riigi nimi peab olema kuni 50 tähemärki")
    private String country;

    @NotBlank(message = "Telefoninumber on kohustuslik")
    @Pattern(regexp = "^\\+?[0-9\\s]{8,15}$", message = "Palun sisesta korrektne telefoninumber")
    private String phoneNumber;
}
