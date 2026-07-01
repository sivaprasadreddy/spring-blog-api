package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.posts.domain.models.CommentDto;
import com.sivalabs.blog.posts.domain.models.PostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PostMapper {
    PostDto toPostDto(PostProjection p);
    CommentDto toCommentDto(Comment comment);
}
