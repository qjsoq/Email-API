package com.example.emailapp.web.dto.user;

import com.example.emailapp.web.dto.mailbox.MailBoxDto;
import lombok.Data;

@Data
public class UserDto {
    private String login;
    private MailBoxDto mailBoxDto;
}
