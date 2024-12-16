package com.example.emailapp.service;

import javax.mail.Message;

public interface ImapService {
    Message[] getEmails(String account, String folderName) throws Exception;
    Message getEmail(String account, String folderName, int msgnum) throws Exception;
    void moveEmail(String account, String sourceFolder, String destinationFolder, int msgnum)
            throws Exception;
    boolean createFolder(String folderName, String account) throws Exception;

}
