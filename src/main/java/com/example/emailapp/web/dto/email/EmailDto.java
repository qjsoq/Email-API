package com.example.emailapp.web.dto.email;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EmailDto {
    private String subject;
    private LocalDateTime sentAt;
}
