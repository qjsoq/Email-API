package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Email;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.SendStrategy;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import java.time.LocalDateTime;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("gmail")
public class GmailSendStrategy implements SendStrategy {
    private final MailBoxRepository mailBoxRepository;
    private final Properties props;

    public GmailSendStrategy(MailBoxRepository mailBoxRepository, @Qualifier("gmail-properties") Properties props) {
        this.mailBoxRepository = mailBoxRepository;
        this.props = props;
    }

    @Override
    public Email sendWithStrategyEmail(Email email) throws MessagingException, UnsupportedEncodingException {
        var mailBox = mailBoxRepository.findByEmailAddress(email.getSenderEmail())
                .orElseThrow(RuntimeException::new);
        var configs = mailBox.getEmailConfiguration();


        Session session = Session.getInstance(props);
        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSenderEmail(), mailBox.getUser().getLogin()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientEmail()));
        msg.setSubject(email.getSubject());
        msg.setContent(email.getBody(), "text/html");

        try (SMTPTransport transport = new SMTPTransport(session, null)) {
            transport.connect(configs.getHost(), email.getSenderEmail(), null);
            transport.issueCommand(
                    "AUTH XOAUTH2 " + new String(BASE64EncoderStream.encode(
                            String.format("user=%s\1auth=Bearer %s\1\1",
                                    email.getSenderEmail(),
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
