package com.online.eShopping.mapper;

import com.online.eShopping.dto.CartDTO;
import com.online.eShopping.dto.CartItemDTO;
import com.online.eShopping.model.Cart;
import com.online.eShopping.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    /**
     * Konverteerib Cart üksuse CartDTO-ks.
     * @param cart Algne Cart üksus
     * @return Teisendatud CartDTO
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "items", target = "items")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    CartDTO toDTO(Cart cart);

    /**
     * Konverteerib CartItem üksuse CartItemDTO-ks.
     * @param cartItem Algne CartItem üksus
     * @return Teisendatud CartItemDTO
     */
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "productImage")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem))")
    CartItemDTO cartItemToDTO(CartItem cartItem);

    /**
     * Konverteerib CartItem üksuste nimekirja CartItemDTO-de nimekirjaks.
     * @param cartItems Algne CartItem üksuste nimekiri
     * @return Teisendatud CartItemDTO-de nimekiri
     */
    List<CartItemDTO> cartItemsToDTOs(List<CartItem> cartItems);

    /**
     * Abimeetod ostukorvi koguhinna arvutamiseks.
     * @param cart Ostukorvi üksus
     * @return Ostukorvi koguhind
     */
    default BigDecimal calculateTotalPrice(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return cart.getItems().stream()
                .map(this::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Abimeetod ostukorvi ühiku kogusumma arvutamiseks.
     * @param cartItem Ostukorvi ühiku üksus
     * @return Ühiku kogusumma
     */
    default BigDecimal calculateSubtotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) {
            return BigDecimal.ZERO;
        }

        return cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
