package com.example.emailapp.web.controller;

import com.example.emailapp.domain.HttpResponse;
import com.example.emailapp.service.UserService;
import com.example.emailapp.web.dto.mailbox.MailBoxCreation;
import com.example.emailapp.web.mapper.MailBoxMapper;
import com.example.emailapp.web.mapper.UserMapper;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final MailBoxMapper mailBoxMapper;

    @PostMapping("/add-account/{login}")
    public ResponseEntity<HttpResponse> addAccount(@RequestBody MailBoxCreation mailBoxCreation,
                                                   @PathVariable String login) {
        var mailBox = userService.addAccount(mailBoxMapper.toMailBox(mailBoxCreation), login);
        return ResponseEntity.created(URI.create("")).body(HttpResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .code(201)
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("new-email-box", mailBoxMapper.toDto(mailBox)))
                .message("Email box was added")
                .path("/api/v1/users/add-account/{login}")
                .build());
    }
}
