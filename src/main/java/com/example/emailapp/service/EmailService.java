package com.example.emailapp.service;

import com.example.emailapp.domain.Email;
import com.example.emailapp.domain.MailBox;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

public interface EmailService {
    void specifyStrategy(String senderEmail);

    Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException;

    boolean addEmailConfiguration(MailBox mailBox);
}
