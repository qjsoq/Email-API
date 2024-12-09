package com.example.emailapp.web.mapper;

import com.example.emailapp.domain.User;
import com.example.emailapp.web.dto.user.UserCreationDto;
import com.example.emailapp.web.dto.user.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User toUser(UserCreationDto dto);

    UserDto toDto(User user);

}
