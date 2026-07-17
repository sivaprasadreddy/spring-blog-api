package com.sivalabs.blog.categories.domain;

import com.sivalabs.blog.categories.domain.models.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
}
