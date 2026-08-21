package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.categories.CategoriesAPI;
import com.sivalabs.blog.categories.domain.models.CategoryDto;
import com.sivalabs.blog.posts.domain.models.*;
import com.sivalabs.blog.shared.exceptions.BadRequestException;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.PagedResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private static final String DEFAULT_CATEGORY_SLUG = "general";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoriesAPI categoriesAPI;
    private final PostMapper postMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ApplicationProperties properties;

    PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            CategoriesAPI categoriesAPI,
            PostMapper postMapper,
            ApplicationEventPublisher eventPublisher,
            ApplicationProperties properties) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.categoriesAPI = categoriesAPI;
        this.postMapper = postMapper;
        this.eventPublisher = eventPublisher;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public PagedResult<PostDto> findPosts(int pageNo) {
        Pageable pageable = this.getPageRequest(pageNo);
        Page<PostDto> posts = postRepository.findPosts(pageable);
        return PagedResult.from(posts);
    }

    @Transactional(readOnly = true)
    public PagedResult<PostDto> searchPosts(String query, int pageNo) {
        Pageable pageable = this.getPageRequest(pageNo);
        Page<PostDto> posts = postRepository.searchPosts("%" + query.toLowerCase() + "%", pageable);
        return PagedResult.from(posts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> findPostsCreatedBetween(Instant start, Instant end) {
        return postRepository.findByCreatedDate(start, end);
    }

    @Transactional(readOnly = true)
    public Optional<PostDto> findPostBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    @Transactional
    public void createPost(CreatePostCmd cmd) {
        if (postRepository.existsBySlug(cmd.slug())) {
            throw new BadRequestException("Post with slug %s already exists".formatted(cmd.slug()));
        }

        var category = getCategoryBySlug(cmd.categorySlug());

        var entity = new Post();
        entity.setTitle(cmd.title());
        entity.setSlug(cmd.slug());
        entity.setContent(cmd.content());
        entity.setCategoryId(category.id());
        postRepository.save(entity);

        var event = new PostPublishedEvent(entity.getTitle(), entity.getSlug(), entity.getContent());
        eventPublisher.publishEvent(event);
    }

    @Transactional
    public void updatePost(UpdatePostCmd cmd) {
        var entity = postRepository
                .findEntityBySlug(cmd.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + cmd.slug() + "' not found"));

        if (!entity.getCreatedBy().equals(cmd.userId())) {
            throw new AccessDeniedException("Only the post owner can modify the post");
        }

        if (!cmd.newSlug().equals(entity.getSlug()) && postRepository.existsBySlug(cmd.newSlug())) {
            throw new BadRequestException("Post with slug %s already exists".formatted(cmd.newSlug()));
        }

        var category = getCategoryBySlug(cmd.categorySlug());

        entity.setTitle(cmd.newTitle());
        entity.setSlug(cmd.newSlug());
        entity.setContent(cmd.newContent());
        entity.setCategoryId(category.id());
        postRepository.save(entity);
    }

    private CategoryDto getCategoryBySlug(String categorySlug) {
        var slug = (categorySlug == null || categorySlug.isBlank()) ? DEFAULT_CATEGORY_SLUG : categorySlug;
        return categoriesAPI
                .findBySlug(slug)
                .orElseThrow(() -> new BadRequestException("Category with slug %s not found".formatted(slug)));
    }

    @Transactional(readOnly = true)
    public PagedResult<CommentDto> getCommentsByPostId(Long postId, int pageNo) {
        Pageable pageable = this.getPageRequest(pageNo);
        var comments = commentRepository.findByPostId(postId, pageable).map(postMapper::toCommentDto);
        return PagedResult.from(comments);
    }

    @Transactional
    public CommentDto createComment(CreateCommentCmd cmd) {
        var post = postRepository.getReferenceById(cmd.postId());
        var entity = new Comment();
        entity.setName(cmd.name());
        entity.setEmail(cmd.email());
        entity.setContent(cmd.content());
        entity.setPost(post);
        commentRepository.save(entity);
        return postMapper.toCommentDto(entity);
    }

    public Optional<CommentDto> getCommentById(Long commentId) {
        return commentRepository.findById(commentId).map(postMapper::toCommentDto);
    }

    private Pageable getPageRequest(int pageNo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageSize = properties.pageSize();
        if (pageNo < 1) {
            pageNo = 1;
        }
        return PageRequest.of(pageNo - 1, pageSize, sort);
    }
}
