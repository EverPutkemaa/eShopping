package com.online.eShopping.mapper;

import com.online.eShopping.dto.AddressDTO;
import com.online.eShopping.dto.OrderCreateDTO;
import com.online.eShopping.dto.OrderDTO;
import com.online.eShopping.dto.OrderItemDTO;
import com.online.eShopping.model.Address;
import com.online.eShopping.model.Order;
import com.online.eShopping.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper Order üksuse ja DTO-de vahel kaardistamiseks.
 */

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface OrderMapper {

    /**
     * Konverteerib Order üksuse OrderDTO-ks.
     * @param order Algne Order üksus
     * @return Teisendatud OrderDTO
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "shippingAddress", target = "shippingAddress")
    OrderDTO toDTO(Order order);

    /**
     * Konverteerib OrderItem üksuse OrderItemDTO-ks.
     * @param orderItem Algne OrderItem üksus
     * @return Teisendatud OrderItemDTO
     */
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "productImage")
    @Mapping(target = "subtotal", expression = "java(orderItem.getSubtotal())")
    OrderItemDTO orderItemToDTO(OrderItem orderItem);

    /**
     * Konverteerib OrderItem üksuste nimekirja OrderItemDTO-de nimekirjaks.
     * @param orderItems Algne OrderItem üksuste nimekiri
     * @return Teisendatud OrderItemDTO-de nimekiri
     */
    List<OrderItemDTO> orderItemsToDTOs(List<OrderItem> orderItems);

    /**
     * Konverteerib Address üksuse AddressDTO-ks.
     * @param address Algne Address üksus
     * @return Teisendatud AddressDTO
     */
    AddressDTO addressToDTO(Address address);

    /**
     * Konverteerib AddressDTO Address üksuseks.
     * @param addressDTO Algne AddressDTO
     * @return Teisendatud Address üksus
     */
    Address toAddressEntity(AddressDTO addressDTO);

    /**
     * Konverteerib Order üksuste nimekirja OrderDTO-de nimekirjaks.
     * @param orders Algne Order üksuste nimekiri
     * @return Teisendatud OrderDTO-de nimekiri
     */
    List<OrderDTO> toDTOList(List<Order> orders);

    /**
     * Uuendab olemasolevat Order üksust OrderCreateDTO andmetega.
     * Kasutatakse uue tellimuse loomisel.
     * @param orderCreateDTO Tellimuse loomise andmed
     * @param order Tellimuse üksus, mida uuendada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(source = "shippingAddress", target = "shippingAddress")
    @Mapping(source = "shippingMethod", target = "shippingMethod")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateOrderFromDTO(OrderCreateDTO orderCreateDTO, @MappingTarget Order order);

    /**
     * Täiendav meetod tellimuse üksuse ja DTO teisenduse kohandamiseks.
     * Võimaldab soovi korral rakendada täiendavaid teisendusreegleid.
     */
    @AfterMapping
    default void afterToDTO(Order order, @MappingTarget OrderDTO orderDTO) {
        // Siin saab vajadusel rakendada täiendavaid teisendusreegleid
    }
}
