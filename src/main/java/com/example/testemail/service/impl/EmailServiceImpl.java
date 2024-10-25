package com.example.testemail.service.impl;

import static com.example.testemail.utils.EmailUtils.*;

import com.example.testemail.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public static final String UTF_8 = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailTemplate";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromMail;
    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        System.out.println(host + mailSender);
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("test email");
            message.setFrom(fromMail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, token));
            mailSender.send(message);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachment(String name, String to, String token) {
        try{
            var message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
            helper.setPriority(1);
            helper.setSubject("New user account verification");
            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));
            FileSystemResource stylejpg = new FileSystemResource(new File("D:/Downloads" +
                    "/image.jpg"));
            helper.addInline(getContentId(stylejpg.getFilename()), stylejpg);
            mailSender.send(message);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String name, String to, String token) {
        try{
            Context context = new Context();
            context.setVariables(Map.of("name", name,"url", getVerificationURL(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            var message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);
            helper.setPriority(1);
            helper.setSubject("Test email");
            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
    private String getContentId(String file) {
        return "<" + file + ">";
    }
}
