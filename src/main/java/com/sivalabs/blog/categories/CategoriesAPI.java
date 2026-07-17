package com.sivalabs.blog.categories;

import com.sivalabs.blog.categories.domain.CategoryService;
import com.sivalabs.blog.categories.domain.models.CategoryDto;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CategoriesAPI {
    private final CategoryService categoryService;

    CategoriesAPI(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Optional<CategoryDto> findBySlug(String slug) {
        return categoryService.findCategoryBySlug(slug);
    }
}
