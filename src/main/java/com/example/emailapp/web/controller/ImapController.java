package com.example.emailapp.web.controller;

import com.example.emailapp.domain.HttpResponse;
import com.example.emailapp.service.ImapService;
import javax.mail.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read")
@RequiredArgsConstructor
public class ImapController {
    private final ImapService imapService;

    @GetMapping("/{account}/{folderName}")
    public ResponseEntity<HttpResponse> readEmails(@PathVariable String account,
                                                   @PathVariable String folderName)
            throws Exception {
        Message[] messages = imapService.getEmails(account, folderName);
        return ResponseEntity.ok(HttpResponse.builder().build());
    }
}
