package com.example.testemail.service.impl;

import com.example.testemail.domain.Confirmation;
import com.example.testemail.domain.User;
import com.example.testemail.repository.ConfirmationRepository;
import com.example.testemail.repository.UserRepository;
import com.example.testemail.service.EmailService;
import com.example.testemail.service.UserService;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;
    @Override
    public User saveUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        user.setEnable(false);
        userRepository.save(user);
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        emailService.sendHtmlEmail(user.getName(), user.getEmail(), confirmation.getToken());
        //znzc qagu pmwh qwax
        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnable(true);
        userRepository.save(user);
        return Boolean.TRUE;
    }
}
