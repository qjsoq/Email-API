package com.example.emailapp.web.mapper;

import com.example.emailapp.domain.Email;
import com.example.emailapp.web.dto.email.EmailCreationDto;
import com.example.emailapp.web.dto.email.EmailDto;
import org.mapstruct.Mapper;

@Mapper
public interface EmailMapper {
    Email toEmail(EmailCreationDto emailCreationDto);
    EmailDto toDto(Email email);
}
