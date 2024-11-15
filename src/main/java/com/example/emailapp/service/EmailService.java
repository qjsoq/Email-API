package com.example.emailapp.service;

import com.example.emailapp.domain.Email;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

public interface EmailService {
    Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException;
}
