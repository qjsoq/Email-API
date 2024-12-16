package com.example.emailapp.service;

import com.example.emailapp.domain.Email;
import com.example.emailapp.domain.MailBox;
import com.example.emailapp.web.dto.email.DetailedReceivedEmail;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

public interface EmailService {

    Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException;

    DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum)
            throws Exception;

    boolean addEmailConfiguration(MailBox mailBox);
}
