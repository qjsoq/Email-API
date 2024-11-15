package com.example.emailapp.web.mapper;

import com.example.emailapp.domain.MailBox;
import com.example.emailapp.web.dto.mailbox.MailBoxCreation;
import com.example.emailapp.web.dto.mailbox.MailBoxDto;
import org.mapstruct.Mapper;

@Mapper
public interface MailBoxMapper {
    MailBox toMailBox(MailBoxCreation mailBoxCreation);
    MailBoxDto toDto(MailBox mailBox);
}
