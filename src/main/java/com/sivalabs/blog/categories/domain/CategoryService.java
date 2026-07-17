package com.sivalabs.blog.categories.domain;

import com.sivalabs.blog.categories.domain.models.CategoryDto;
import com.sivalabs.blog.categories.domain.models.CreateCategoryCmd;
import com.sivalabs.blog.categories.domain.models.UpdateCategoryCmd;
import com.sivalabs.blog.shared.exceptions.BadRequestException;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories() {
        return categoryRepository.findAllCategories();
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDto> findCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryCmd cmd) {
        if (categoryRepository.existsBySlug(cmd.slug())) {
            throw new BadRequestException("Category with slug %s already exists".formatted(cmd.slug()));
        }

        var entity = new Category();
        entity.setName(cmd.name());
        entity.setSlug(cmd.slug());
        categoryRepository.save(entity);
        return categoryMapper.toCategoryDto(entity);
    }

    @Transactional
    public CategoryDto updateCategory(UpdateCategoryCmd cmd) {
        var entity = categoryRepository
                .findEntityBySlug(cmd.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Category with slug '" + cmd.slug() + "' not found"));

        if (!cmd.newSlug().equals(entity.getSlug()) && categoryRepository.existsBySlug(cmd.newSlug())) {
            throw new BadRequestException("Category with slug %s already exists".formatted(cmd.newSlug()));
        }

        entity.setName(cmd.newName());
        entity.setSlug(cmd.newSlug());
        categoryRepository.save(entity);
        return categoryMapper.toCategoryDto(entity);
    }

    @Transactional
    public void deleteCategory(String slug) {
        var entity = categoryRepository
                .findEntityBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category with slug '" + slug + "' not found"));
        try {
            categoryRepository.delete(entity);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Category cannot be deleted because it is associated with one or more posts");
        }
    }
}
