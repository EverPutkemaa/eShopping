package com.online.eShopping.service;

import com.online.eShopping.dto.CartDTO;
import com.online.eShopping.dto.CartItemCreateDTO;
import com.online.eShopping.dto.OrderDTO;

public interface CartService {

    /**
     * Tagastab praeguse kasutaja ostukorvi.
     *
     * @return Ostukorvi andmed
     */
    CartDTO getCurrentUserCart();

    /**
     * Lisab toote ostukorvi.
     *
     * @param cartItemDTO Ostukorvi ühiku andmed
     * @return Uuendatud ostukorvi andmed
     */
    CartDTO addItemToCart(CartItemCreateDTO cartItemDTO);

    /**
     * Uuendab toote kogust ostukorvis.
     *
     * @param productId Toote ID
     * @param quantity Uus kogus
     * @return Uuendatud ostukorvi andmed
     */
    CartDTO updateItemQuantity(Long productId, Integer quantity);

    /**
     * Eemaldab toote ostukorvist.
     *
     * @param productId Toote ID
     * @return Uuendatud ostukorvi andmed
     */
    CartDTO removeItemFromCart(Long productId);

    /**
     * Tühjendab ostukorvi.
     *
     * @return Tühjendatud ostukorvi andmed
     */
    CartDTO clearCart();

    /**
     * Teisendab ostukorvi tellimuseks.
     *
     * @param shippingAddressId Tarneaadressi ID
     * @param shippingMethod Tarnemeetod
     * @param paymentMethod Maksemeetod
     * @return Loodud tellimuse andmed
     */
    OrderDTO checkoutCart(Long shippingAddressId, String shippingMethod, String paymentMethod);

}
