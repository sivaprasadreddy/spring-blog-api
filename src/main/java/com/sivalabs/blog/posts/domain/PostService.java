package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.posts.domain.models.*;
import com.sivalabs.blog.shared.exceptions.BadRequestException;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.users.UsersAPI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UsersAPI usersAPI;
    private final PostMapper postMapper;
    private final BlogEventPublisher blogEventPublisher;
    private final ApplicationProperties properties;

    PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            UsersAPI usersAPI,
            PostMapper postMapper,
            BlogEventPublisher blogEventPublisher,
            ApplicationProperties properties) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.usersAPI = usersAPI;
        this.postMapper = postMapper;
        this.blogEventPublisher = blogEventPublisher;
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
    public List<PostDto> findPostsCreatedBetween(LocalDateTime start, LocalDateTime end) {
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
        var user = usersAPI.findById(cmd.createdBy()).orElseThrow();

        var entity = new Post();
        entity.setTitle(cmd.title());
        entity.setSlug(cmd.slug());
        entity.setContent(cmd.content());
        entity.setCreatedBy(user.id());
        postRepository.save(entity);

        var event = new PostPublishedEvent(entity.getTitle(), entity.getSlug(), entity.getContent());
        blogEventPublisher.publish(event);
    }

    @Transactional
    public void updatePost(UpdatePostCmd cmd) {
        var entity = postRepository
                .findById(cmd.id())
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + cmd.id() + " not found"));
        entity.setTitle(cmd.title());
        entity.setSlug(cmd.slug());
        entity.setContent(cmd.content());
        postRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(postMapper::toCommentDto)
                .toList();
    }

    @Transactional
    public void createComment(CreateCommentCmd cmd) {
        var post = postRepository.getReferenceById(cmd.postId());
        var entity = new Comment();
        entity.setName(cmd.name());
        entity.setEmail(cmd.email());
        entity.setContent(cmd.content());
        entity.setPost(post);
        commentRepository.save(entity);
    }

    private Pageable getPageRequest(int pageNo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageSize = properties.postsPerPage();
        if (pageNo < 1) {
            pageNo = 1;
        }
        return PageRequest.of(pageNo - 1, pageSize, sort);
    }
}
