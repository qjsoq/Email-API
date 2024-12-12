package com.example.emailapp.web.dto.email;

import java.util.Date;
import lombok.Data;

@Data
public class ReceivedEmail {
    private String sender;
    private String subject;
    private Date receivedDate;

}
