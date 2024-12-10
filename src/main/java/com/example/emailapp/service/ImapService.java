package com.example.emailapp.service;

import javax.mail.Message;

public interface ImapService {
    Message[] getEmails(String account, String folderName) throws Exception;
}
