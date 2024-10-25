package com.example.testemail.service;


import com.example.testemail.domain.User;

public interface UserService {
    User saveUser(User user);
    Boolean verifyToken(String token);
}
