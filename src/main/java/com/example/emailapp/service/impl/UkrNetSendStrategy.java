package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Email;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.SendStrategy;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("ukr")
@RequiredArgsConstructor
public class UkrNetSendStrategy implements SendStrategy {
    private final MailBoxRepository mailBoxRepository;

    @Override
    public Email sendWithStrategyEmail(Email email)
            throws MessagingException, UnsupportedEncodingException {
        var mailBox = mailBoxRepository.findByEmailAddress(email.getSenderEmail())
                .orElseThrow(RuntimeException::new);
        var configs = mailBox.getEmailConfiguration();

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", configs.getHost());
        props.put("mail.smtp.port", configs.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", configs.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(mailBox.getEmailAddress(),
                        mailBox.getAccessSmtp());
            }
        });

        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSenderEmail(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html");

        Transport.send(msg);

        email.setSentAt(LocalDateTime.now());
        return email;
    }

}
