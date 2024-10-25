package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Confirmation;
import com.example.emailapp.domain.User;
import com.example.emailapp.repository.ConfirmationRepository;
import com.example.emailapp.repository.UserRepository;
import com.example.emailapp.service.EmailService;
import com.example.emailapp.service.UserService;
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
