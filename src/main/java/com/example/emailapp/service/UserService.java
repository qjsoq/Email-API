package com.example.emailapp.service;


import com.example.emailapp.domain.User;

public interface UserService {
    User saveUser(User user);
    Boolean verifyToken(String token);
}
