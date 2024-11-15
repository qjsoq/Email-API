package com.example.emailapp.web.dto.mailbox;

import lombok.Data;

@Data
public class MailBoxCreation {
    private String emailAddress;
    private String accessSmtp;
}
