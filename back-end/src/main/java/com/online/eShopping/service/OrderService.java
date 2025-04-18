package com.online.eShopping.service;

import com.online.eShopping.dto.OrderCreateDTO;
import com.online.eShopping.dto.OrderDTO;
import com.online.eShopping.dto.OrderStatusUpdateDTO;
import org.springframework.data.domain.Page;

public interface OrderService {

    /**
     * Loob uue tellimuse.
     *
     * @param orderCreateDTO Tellimuse loomise andmed
     * @return Loodud tellimuse andmed
     */
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO);

    /**
     * Tagastab tellimuse detailid ID järgi.
     *
     * @param id Tellimuse ID
     * @return Tellimuse andmed
     */
    OrderDTO getOrderById(Long id);

    /**
     * Uuendab tellimuse staatust.
     *
     * @param id Tellimuse ID
     * @param statusUpdateDTO Uue staatuse andmed
     * @return Uuendatud tellimuse andmed
     */
    OrderDTO updateOrderStatus(Long id, OrderStatusUpdateDTO statusUpdateDTO);

    /**
     * Tagastab kõik tellimused (administraatori jaoks).
     *
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Tellimuste nimekiri
     */
    Page<OrderDTO> getAllOrders(int page, int size);

    /**
     * Tagastab konkreetse kasutaja tellimused.
     *
     * @param userId Kasutaja ID
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Kasutaja tellimuste nimekiri
     */
    Page<OrderDTO> getUserOrders(Long userId, int page, int size);

    /**
     * Tagastab praeguse kasutaja tellimused.
     *
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Kasutaja tellimuste nimekiri
     */
    Page<OrderDTO> getCurrentUserOrders(int page, int size);

    /**
     * Tühistab tellimuse, kui see on võimalik.
     *
     * @param id Tellimuse ID
     * @return Tühistatud tellimuse andmed
     */
    OrderDTO cancelOrder(Long id);

}
