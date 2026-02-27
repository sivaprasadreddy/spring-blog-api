package com.sivalabs.blog.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(User entity);

    UserDto toUserDtoWithPassword(User entity);
}
