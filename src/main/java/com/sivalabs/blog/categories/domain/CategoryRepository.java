package com.sivalabs.blog.categories.domain;

import com.sivalabs.blog.categories.domain.models.CategoryDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
        select new com.sivalabs.blog.categories.domain.models.CategoryDto(c.id, c.name, c.slug, c.createdAt, c.updatedAt)
        from Category c
        order by c.name
    """)
    List<CategoryDto> findAllCategories();

    @Query("""
        select new com.sivalabs.blog.categories.domain.models.CategoryDto(c.id, c.name, c.slug, c.createdAt, c.updatedAt)
        from Category c
        where c.slug = :slug
    """)
    Optional<CategoryDto> findBySlug(String slug);

    Optional<Category> findEntityBySlug(String slug);

    boolean existsBySlug(String slug);
}
