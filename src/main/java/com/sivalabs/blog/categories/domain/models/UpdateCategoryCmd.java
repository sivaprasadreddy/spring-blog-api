package com.sivalabs.blog.categories.domain.models;

public record UpdateCategoryCmd(String slug, String newName, String newSlug) {}
