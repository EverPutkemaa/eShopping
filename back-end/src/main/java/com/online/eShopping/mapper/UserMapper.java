package com.online.eShopping.mapper;

import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserRegistrationDTO;
import com.online.eShopping.dto.UserUpdateDTO;
import com.online.eShopping.model.Role;
import com.online.eShopping.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper User üksuse ja DTO-de vahel kaardistamiseks.
 */

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Konverteerib User üksuse UserDTO-ks.
     * @param user Algne User üksus
     * @return Teisendatud UserDTO
     */
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDTO toDTO(User user);

    /**
     * Konverteerib UserRegistrationDTO User üksuseks.
     * @param registrationDTO UserRegistrationDTO üksus
     * @return Teisendatud User üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRegistrationDTO registrationDTO);

    /**
     * Uuendab olemasolevat User üksust UserUpdateDTO andmetega.
     * @param userUpdateDTO Uuendatavad andmed
     * @param user Olemasolev User üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    /**
     * Abimeetod rollide teisendamiseks.
     * Konverteerib Role objektide hulga sõnede hulgaks.
     * @param roles Role objektide hulk
     * @return Sõnede hulk, mis sisaldab rollide nimesid
     */
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

}
