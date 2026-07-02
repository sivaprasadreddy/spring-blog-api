package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.posts.domain.models.PostDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select new com.sivalabs.blog.posts.domain.models.PostDto(p.id, p.title, p.slug, p.content, p.createdBy, u.name, p.createdAt, p.updatedAt)
        from Post p join User u on p.createdBy = u.id
        where p.slug = :slug
    """)
    Optional<PostDto> findBySlug(@Param("slug") String slug);

    @Query("""
        select new com.sivalabs.blog.posts.domain.models.PostDto(p.id, p.title, p.slug, p.content, p.createdBy, u.name, p.createdAt, p.updatedAt)
        from Post p join User u on p.createdBy = u.id
    """)
    Page<PostDto> findPosts(Pageable pageable);

    @Query("""
        select new com.sivalabs.blog.posts.domain.models.PostDto(p.id, p.title, p.slug, p.content, p.createdBy, u.name, p.createdAt, p.updatedAt)
        from Post p join User u on p.createdBy = u.id
        where lower(p.title) like :query or lower(p.content) like :query
    """)
    Page<PostDto> searchPosts(@Param("query") String query, Pageable pageable);

    @Query("""
        select new com.sivalabs.blog.posts.domain.models.PostDto(p.id, p.title, p.slug, p.content, p.createdBy, u.name, p.createdAt, p.updatedAt)
        from Post p join User u on p.createdBy = u.id
        where p.createdAt >= :start and p.createdAt <= :end
    """)
    List<PostDto> findByCreatedDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsBySlug(String slug);
}
