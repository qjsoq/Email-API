package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Email;
import com.example.emailapp.domain.MailBox;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.EmailService;
import com.example.emailapp.service.SendStrategy;
import com.example.emailapp.utils.EmailConfiguration;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailBoxRepository mailBoxRepository;
    private final List<SendStrategy> listOfStrategies;

    private SendStrategy sendStrategy;

    @Override
    public void specifyStrategy(String senderEmail) {
        MailBox mailBox = mailBoxRepository.findByEmailAddress(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender email not found: " + senderEmail));

        String strategyBeanName = mailBox.getEmailConfiguration().getDomainName();

        sendStrategy = listOfStrategies.stream()
                .filter(strategy -> strategy.getClass().isAnnotationPresent(Service.class) &&
                        strategy.getClass().getAnnotation(Service.class).value()
                                .equalsIgnoreCase(strategyBeanName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No SendStrategy found for: " + strategyBeanName));
    }

    @Override
    public Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException {
        specifyStrategy(email.getSenderEmail());

        return sendStrategy.sendWithStrategyEmail(email);
    }

    @Override
    public boolean addEmailConfiguration(MailBox mailBox) {
        String domainPart = getEmailDomain(mailBox.getEmailAddress());
        Optional<EmailConfiguration> appropriateConfig =
                Arrays.stream(EmailConfiguration.values())
                        .filter(config -> config.getDomainName().equalsIgnoreCase(domainPart))
                        .findFirst();
        if (appropriateConfig.isPresent()) {
            mailBox.setEmailConfiguration(appropriateConfig.get());
            return true;
        }
        return false;
    }

    private String getEmailDomain(String emailAddress) {
        Pattern pattern = Pattern.compile("(?<=@)[^.]+(?=\\.)");
        Matcher matcher = pattern.matcher(emailAddress);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalArgumentException("Invalid email address format: " + emailAddress);
        }
    }

}
