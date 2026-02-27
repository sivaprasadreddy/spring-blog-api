package com.sivalabs.blog.posts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.id = :id")
    Optional<PostProjection> findPostById(@Param("id") Long id);

    @Query("select p from Post p where p.slug = :slug")
    Optional<PostProjection> findBySlug(@Param("slug") String slug);

    @Query("select p from Post p")
    Page<PostProjection> findPosts(Pageable pageable);

    @Query("""
        select p from Post p
        where lower(p.title) like :query or lower(p.content) like :query
    """)
    Page<PostProjection> searchPosts(@Param("query") String query, Pageable pageable);

    @Query("""
        select p from Post p
        where p.createdAt >= :start and p.createdAt <= :end
    """)
    List<PostProjection> findByCreatedDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsBySlug(String slug);
}
