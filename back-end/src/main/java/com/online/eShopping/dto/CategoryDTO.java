package com.online.eShopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private List<CategoryDTO> subcategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
