package com.online.eShopping.mapper;

import com.online.eShopping.dto.*;
import com.online.eShopping.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    /**
     * Konverteerib Product üksuse ProductDTO-ks.
     * @param product Algne Product üksus
     * @return Teisendatud ProductDTO
     */
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductDTO toDTO(Product product);

    /**
     * Konverteerib ProductCreateDTO Product üksuseks.
     * @param productCreateDTO ProductCreateDTO üksus
     * @return Teisendatud Product üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductCreateDTO productCreateDTO);

    /**
     * Uuendab olemasolevat Product üksust ProductUpdateDTO andmetega.
     * @param productUpdateDTO Uuendatavad andmed
     * @param product Olemasolev Product üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
}
