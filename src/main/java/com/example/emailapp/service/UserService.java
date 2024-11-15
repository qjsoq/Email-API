package com.example.emailapp.service;


import com.example.emailapp.domain.MailBox;
import com.example.emailapp.domain.User;

public interface UserService {
    User saveUser(User user);
    Boolean verifyToken(String token);
    MailBox addAccount(MailBox MailBox, String login);

}
