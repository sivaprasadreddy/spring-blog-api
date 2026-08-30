package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.shared.entities.BaseEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "posts")
class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_generator")
    @SequenceGenerator(name = "post_id_generator", sequenceName = "post_id_seq")
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private Long createdBy;

    @Column(name = "updated_by")
    @LastModifiedBy
    private Long updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
