package com.sivalabs.blog.categories.api;

import com.sivalabs.blog.categories.domain.CategoryService;
import com.sivalabs.blog.categories.domain.models.CategoryDto;
import com.sivalabs.blog.categories.domain.models.CreateCategoryCmd;
import com.sivalabs.blog.categories.domain.models.UpdateCategoryCmd;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Categories API")
class CategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Find categories", description = "Returns the list of all categories")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Returns the list of categories"),
    })
    List<CategoryDto> findCategories() {
        LOG.info("Get all categories");
        return categoryService.findAllCategories();
    }

    @GetMapping("/categories/{slug}")
    @Operation(summary = "Get category by slug")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Returns the category with the given slug"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
    })
    ResponseEntity<CategoryDto> getCategoryBySlug(@PathVariable String slug) {
        LOG.info("Get category by slug='{}'", slug);
        var category = categoryService
                .findCategoryBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category with slug '" + slug + "' not found"));
        return ResponseEntity.ok(category);
    }

    @PostMapping(value = "/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "Bearer")
    @Operation(summary = "Create category")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category payload"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
    })
    ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryPayload payload) {
        LOG.info("Creating a new category with slug: '{}'", payload.slug());
        var cmd = new CreateCategoryCmd(payload.name(), payload.slug());
        var category = categoryService.createCategory(cmd);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/categories/{slug}")
                .buildAndExpand(category.slug())
                .toUri();
        return ResponseEntity.created(location).body(category);
    }

    @PutMapping(value = "/categories/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "Bearer")
    @Operation(summary = "Update category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category payload"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
    })
    ResponseEntity<CategoryDto> updateCategory(
            @PathVariable String slug, @Valid @RequestBody UpdateCategoryPayload payload) {
        LOG.info("Updating category with slug: '{}'", slug);
        var cmd = new UpdateCategoryCmd(slug, payload.name(), payload.slug());
        var category = categoryService.updateCategory(cmd);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @DeleteMapping("/categories/{slug}")
    @SecurityRequirement(name = "Bearer")
    @Operation(summary = "Delete category")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
    })
    ResponseEntity<Void> deleteCategory(@PathVariable String slug) {
        LOG.info("Deleting category with slug: '{}'", slug);
        categoryService.deleteCategory(slug);
        return ResponseEntity.noContent().build();
    }
}
