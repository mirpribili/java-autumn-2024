package ru.tbank.mapper;

import ru.tbank.dto.CategoryDTO;
import ru.tbank.model.Category;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getSlug(), category.getName());
    }

    public static Category toEntity(CategoryDTO categoryDTO) {
        return new Category(0, categoryDTO.getSlug(), categoryDTO.getName()); // ID будет установлен при сохранении
    }
}