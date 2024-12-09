package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Confirmation;
import com.example.emailapp.domain.MailBox;
import com.example.emailapp.domain.User;
import com.example.emailapp.repository.ConfirmationRepository;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.repository.UserRepository;
import com.example.emailapp.service.EmailService;
import com.example.emailapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;
    private final MailBoxRepository mailBoxRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new RuntimeException("Email already exists");
        }
        user.setEnable(false);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByLoginIgnoreCase(confirmation.getUser().getLogin());
        user.setEnable(true);
        userRepository.save(user);
        return Boolean.TRUE;
    }

    @Override
    public MailBox addAccount(MailBox mailBox, String login) {
        var user = userRepository.findByLoginIgnoreCase(login);
        emailService.addEmailConfiguration(mailBox);
        mailBox.setUser(user);
        user.getUserEmails().add(mailBox);
        mailBoxRepository.save(mailBox);
        return mailBox;
    }
}
