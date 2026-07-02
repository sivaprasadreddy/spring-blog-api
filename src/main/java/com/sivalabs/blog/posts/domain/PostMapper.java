package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.posts.domain.models.CommentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PostMapper {
    CommentDto toCommentDto(Comment comment);
}
