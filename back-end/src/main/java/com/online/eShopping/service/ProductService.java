package com.online.eShopping.service;

import com.online.eShopping.dto.ProductCreateDTO;
import com.online.eShopping.dto.ProductDTO;
import com.online.eShopping.dto.ProductUpdateDTO;
import org.springframework.data.domain.Page;

public interface ProductService {

    /**
     * Tagastab kõik tooted lehekülgedena.
     *
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Leht ProductDTO objektidega
     */
    Page<ProductDTO> getAllProducts(int page, int size);

    /**
     * Leiab toote ID järgi.
     *
     * @param id Toote ID
     * @return Leitud toode või visatakse ResourceNotFoundException
     */
    ProductDTO getProductById(Long id);

    /**
     * Leiab tooted kategooria järgi.
     *
     * @param categoryId Kategooria ID
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Leht ProductDTO objektidega, mis kuuluvad antud kategooriasse
     */
    Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size);

    /**
     * Otsib tooteid märksõna järgi (nimi või kirjeldus).
     *
     * @param keyword Otsingu märksõna
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Leht ProductDTO objektidega, mis vastavad otsingukriteeriumitele
     */
    Page<ProductDTO> searchProducts(String keyword, int page, int size);

    /**
     * Loob uue toote.
     *
     * @param productCreateDTO Loodava toote andmed
     * @return Loodud toote andmed
     */
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);

    /**
     * Uuendab olemasolevat toodet.
     *
     * @param id Uuendatava toote ID
     * @param productUpdateDTO Uuendatud toote andmed
     * @return Uuendatud toote andmed
     */
    ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO);

    /**
     * Kustutab toote.
     *
     * @param id Kustutatava toote ID
     */
    void deleteProduct(Long id);

    /**
     * Vähendab toote laovarude seisu.
     *
     * @param productId Toote ID
     * @param quantity Kogus, mille võrra vähendada
     * @throws//  InsufficientStockException Kui laovaru on ebapiisav
     */
    void reduceStock(Long productId, int quantity);

    /**
     * Kontrollib, kas toode on laos saadaval soovitud koguses.
     *
     * @param productId Toote ID
     * @param quantity Soovitud kogus
     * @return true, kui toode on saadaval; false, kui ei ole
     */
    boolean isInStock(Long productId, int quantity);
}
