package com.sivalabs.blog.posts;

import com.sivalabs.blog.shared.BadRequestException;
import com.sivalabs.blog.shared.PagedResult;
import com.sivalabs.blog.shared.ResourceNotFoundException;
import com.sivalabs.blog.users.UserContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts API")
class PostController {
    private static final Logger LOG = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    PagedResult<PostDto> findPosts(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        LOG.info("Get posts by page='{}' and query='{}'", page, query);
        if (query == null || query.trim().isEmpty()) {
            return postService.findPosts(page);
        }
        return postService.searchPosts(query, page);
    }

    @GetMapping("/{slug}")
    ResponseEntity<PostDto> getPostBySlug(@PathVariable String slug) {
        LOG.info("Get post by slug='{}'", slug);
        var post = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{slug}/comments")
    List<CommentDto> getPostComments(@PathVariable String slug) {
        LOG.info("Get post comments by slug='{}'", slug);
        PostDto postDto = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        return postService.getCommentsByPostId(postDto.id());
    }

    @PostMapping("/{slug}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    void createComment(@PathVariable String slug, @Valid @RequestBody CreateCommentPayload payload) {
        LOG.info("Create comment for post with slug: '{}'", slug);
        PostDto postDto = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        var createdCommentCmd = new CreateCommentCmd(payload.name, payload.email, payload.content, postDto.id());
        postService.createComment(createdCommentCmd);
    }

    record CreateCommentPayload(
            @NotEmpty(message = "Name is required")
            String name,

            @NotEmpty(message = "Email is required")
            @Email(message = "Invalid email address")
            String email,

            @NotEmpty(message = "Content is required")
            String content) {}

    @PostMapping("")
    @SecurityRequirement(name = "Bearer")
    ResponseEntity<Void> createPost(@Valid @RequestBody PostPayload postPayload) {
        var loginUserId = UserContextUtils.getCurrentUserIdOrThrow();
        var slug = postPayload.slug();
        LOG.info("Creating a new post with slug: '{}'", slug);
        var cmd = new CreatePostCmd(postPayload.title(), slug, postPayload.content(), loginUserId);
        this.postService.createPost(cmd);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/posts/{slug}")
                .buildAndExpand(slug)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{slug}")
    @SecurityRequirement(name = "Bearer")
    ResponseEntity<Void> updatePost(@PathVariable String slug, @Valid @RequestBody PostPayload postPayload) {
        LOG.info("Updating post with slug: '{}'", slug);
        PostDto postDto = postService
                .findPostBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post with slug '" + slug + "' not found"));
        var updatedSlug = postPayload.slug();
        Optional<PostDto> postBySlug = postService.findPostBySlug(updatedSlug);
        if (postBySlug.isPresent() && !Objects.equals(postBySlug.get().id(), postDto.id())) {
            throw new BadRequestException("Post with slug '" + updatedSlug + "' already exists");
        }
        var loginUserId = UserContextUtils.getCurrentUserIdOrThrow();
        var cmd = new UpdatePostCmd(postDto.id(), postPayload.title(), updatedSlug, postPayload.content(), loginUserId);
        this.postService.updatePost(cmd);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(null)
                .path("/api/posts/{slug}")
                .buildAndExpand(updatedSlug)
                .toUri();
        return ResponseEntity.status(HttpStatus.OK).location(location).build();
    }

    record PostPayload(
            @NotEmpty(message = "Title is required") String title,
            @NotEmpty(message = "Slug is required") String slug,
            @NotEmpty(message = "Content is required") String content) {}
}
