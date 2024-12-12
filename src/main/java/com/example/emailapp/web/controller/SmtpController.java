package com.example.emailapp.web.controller;

import com.example.emailapp.domain.HttpResponse;
import com.example.emailapp.service.EmailService;
import com.example.emailapp.web.dto.email.EmailCreationDto;
import com.example.emailapp.web.mapper.EmailMapper;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/send")
@RequiredArgsConstructor
public class SmtpController {
    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @PostMapping
    public ResponseEntity<HttpResponse> sendEmail(@RequestBody EmailCreationDto emailCreationDto)
            throws UnsupportedEncodingException, MessagingException {
        var email = emailService.sendEmail(emailMapper.toEmail(emailCreationDto));
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .data(Map.of("email", emailMapper.toDto(email)))
                .path("/api/v1/emails")
                .timeStamp(LocalDateTime.now().toString())
                .build());
    }

}
