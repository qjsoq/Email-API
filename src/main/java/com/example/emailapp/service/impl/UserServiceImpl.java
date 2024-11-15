package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Confirmation;
import com.example.emailapp.domain.MailBox;
import com.example.emailapp.domain.User;
import com.example.emailapp.repository.ConfirmationRepository;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.repository.UserRepository;
import com.example.emailapp.service.UserService;
import com.example.emailapp.utils.EmailConfiguration;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
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
        addEmailConfiguration(mailBox);
        mailBox.setUser(user);
        user.getUserEmails().add(mailBox);
        mailBoxRepository.save(mailBox);
        return mailBox;
    }

    public boolean addEmailConfiguration(MailBox MailBox) {
        String email = MailBox.getEmailAddress();
        Pattern pattern = Pattern.compile("(?<=@)[^.]+(?=\\.)");
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) {
            String domainPart = matcher.group();
            Optional<EmailConfiguration> appropriateConfig =
                    Arrays.stream(EmailConfiguration.values())
                            .filter(config -> config.getName().equalsIgnoreCase(domainPart))
                            .findFirst();
            if (appropriateConfig.isPresent()) {
                MailBox.setEmailConfiguration(appropriateConfig.get());
                return true;
            }
        }
        return false;
    }

}
