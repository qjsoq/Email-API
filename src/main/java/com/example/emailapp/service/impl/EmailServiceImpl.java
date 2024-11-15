package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Email;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.EmailService;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final MailBoxRepository mailBoxRepository;

    @Override
    public Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException {
        var mailBox = mailBoxRepository.findByEmailAddress(email.getSender())
                .orElseThrow(RuntimeException::new);
        var configs = mailBox.getEmailConfiguration();
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", configs.getPort());
        props.put("mail.smtp.starttls.enable", configs.isUseTls());
        System.out.println(configs.getPort());
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        System.out.println(email.getSender());
        System.out.println(email.getBody());
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSender(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipient()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html");

        try (SMTPTransport transport = new SMTPTransport(session, null)) {
            transport.connect(configs.getHost(), email.getSender(), null);
            transport.issueCommand(
                    "AUTH XOAUTH2 " + new String(BASE64EncoderStream.encode(
                            String.format("user=%s\1auth=Bearer %s\1\1",
                                    email.getSender(),
                                    mailBox.getAccessSmtp()).getBytes())),
                    235);
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException exception) {
            throw exception;
        }
        email.setSentAt(LocalDateTime.now());
        return email;
    }

}
