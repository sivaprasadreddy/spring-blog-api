package com.sivalabs.blog.posts;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PostMapper {
    PostDto toPostDto(PostProjection p);
    CommentDto toCommentDto(Comment comment);
}
