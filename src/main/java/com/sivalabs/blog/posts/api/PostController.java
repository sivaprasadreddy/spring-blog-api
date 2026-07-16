package com.sivalabs.blog.posts.api;

import com.sivalabs.blog.posts.domain.PostService;
import com.sivalabs.blog.posts.domain.models.*;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.utils.AuthUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Posts API")
class PostController {
    private static final Logger LOG = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    PagedResult<PostDto> findPosts(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        LOG.info("Get posts by page='{}' and query='{}'", page, query);
        if (query == null || query.trim().isEmpty()) {
            return postService.findPosts(page);
        }
        return postService.searchPosts(query, page);
    }

    @GetMapping("/posts/{slug}")
    ResponseEntity<PostDto> getPostBySlug(@PathVariable String slug) {
        LOG.info("Get post by slug='{}'", slug);
        var post = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        return ResponseEntity.ok(post);
    }

    @PostMapping(value = "/posts", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "Bearer")
    ResponseEntity<Void> createPost(@Valid @RequestBody PostPayload postPayload) {
        var slug = postPayload.slug();
        LOG.info("Creating a new post with slug: '{}'", slug);
        var cmd = new CreatePostCmd(postPayload.title(), slug, postPayload.content());
        this.postService.createPost(cmd);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/posts/{slug}")
                .buildAndExpand(slug)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/posts/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "Bearer")
    ResponseEntity<Void> updatePost(@PathVariable String slug, @Valid @RequestBody PostPayload postPayload) {
        LOG.info("Updating post with slug: '{}'", slug);
        var loginUserId = AuthUtils.getCurrentUserIdOrThrow();

        var cmd = new UpdatePostCmd(slug, postPayload.title(), postPayload.slug(), postPayload.content(), loginUserId);
        this.postService.updatePost(cmd);

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/posts/{slug}")
                .buildAndExpand(postPayload.slug())
                .toUri();
        return ResponseEntity.status(HttpStatus.OK).location(location).build();
    }

    @GetMapping("/posts/{slug}/comments")
    PagedResult<CommentDto> getPostComments(
            @PathVariable String slug, @RequestParam(value = "page", defaultValue = "1") int page) {
        LOG.info("Get post comments by slug='{}'", slug);
        PostDto postDto = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        return postService.getCommentsByPostId(postDto.id(), page);
    }

    @PostMapping(value = "/posts/{slug}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createComment(@PathVariable String slug, @Valid @RequestBody CreateCommentPayload payload) {
        LOG.info("Create comment for post with slug: '{}'", slug);
        PostDto postDto = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        var createdCommentCmd = new CreateCommentCmd(payload.name(), payload.email(), payload.content(), postDto.id());
        CommentDto comment = postService.createComment(createdCommentCmd);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/comments/{commentId}")
                .buildAndExpand(comment.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/comments/{commentId}")
    ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        LOG.info("Get comment by commentId={}", commentId);
        CommentDto commentDto = postService
                .getCommentById(commentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Comment with commentId '" + commentId + "' not found"));
        return ResponseEntity.ok(commentDto);
    }
}
