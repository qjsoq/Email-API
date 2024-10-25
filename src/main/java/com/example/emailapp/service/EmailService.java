package com.example.emailapp.service;

public interface EmailService {
    public void sendSimpleMailMessage(String name, String to, String token);
    public void sendMimeMessageWithAttachment(String name, String to, String token);
    public void sendHtmlEmail(String name, String to, String token);

}
