package com.online.eShopping.mapper;

import com.online.eShopping.dto.CategoryCreateDTO;
import com.online.eShopping.dto.CategoryDTO;
import com.online.eShopping.dto.CategoryUpdateDTO;
import com.online.eShopping.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper {

    /**
     * Konverteerib Category üksuse CategoryDTO-ks.
     * @param category Algne Category üksus
     * @return Teisendatud CategoryDTO
     */
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    @Mapping(target = "subcategories", ignore = true)
    CategoryDTO toDTO(Category category);

    /**
     * Konverteerib CategoryCreateDTO Category üksuseks.
     * @param categoryCreateDTO CategoryCreateDTO üksus
     * @return Teisendatud Category üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryCreateDTO categoryCreateDTO);

    /**
     * Uuendab olemasolevat Category üksust CategoryUpdateDTO andmetega.
     * @param categoryUpdateDTO Uuendatavad andmed
     * @param category Olemasolev Category üksus
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(CategoryUpdateDTO categoryUpdateDTO, @MappingTarget Category category);

    /**
     * Konverteerib Category üksuste nimekirja CategoryDTO-de nimekirjaks.
     * @param categories Algne Category üksuste nimekiri
     * @return Teisendatud CategoryDTO-de nimekiri
     */
    List<CategoryDTO> toDTOList(List<Category> categories);

}
