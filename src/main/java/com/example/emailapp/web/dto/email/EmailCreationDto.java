package com.example.emailapp.web.dto.email;

import lombok.Data;

@Data
public class EmailCreationDto {
    private String sender;
    private String recipient;
    private String subject;
    private String body;
}
