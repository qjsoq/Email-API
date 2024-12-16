package com.example.emailapp.service.impl;

import com.example.emailapp.domain.Email;
import com.example.emailapp.domain.MailBox;
import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.EmailService;
import com.example.emailapp.service.ImapService;
import com.example.emailapp.service.SendStrategy;
import com.example.emailapp.utils.EmailConfiguration;
import com.example.emailapp.web.dto.email.DetailedReceivedEmail;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailBoxRepository mailBoxRepository;
    private final List<SendStrategy> listOfStrategies;
    private final ImapService imapService;
    private SendStrategy sendStrategy;


    @Override
    public Email sendEmail(Email email) throws MessagingException, UnsupportedEncodingException {
        specifyStrategy(email.getSenderEmail());

        return sendStrategy.sendWithStrategyEmail(email);
    }

    @Override
    public DetailedReceivedEmail getSpecificEmail(String account, String folderName, int msgnum)
            throws Exception {
        Message message = imapService.getEmail(account, folderName, msgnum);

        Address[] fromAddresses = message.getFrom();
        String senderEmail = null;
        if (fromAddresses != null && fromAddresses.length > 0) {
            InternetAddress address = (InternetAddress) fromAddresses[0];
            senderEmail = address.getAddress();
        }

        Address[] recipientAddresses = message.getAllRecipients();
        String receiverEmail = null;
        if (recipientAddresses != null && recipientAddresses.length > 0) {
            InternetAddress address = (InternetAddress) recipientAddresses[0];
            receiverEmail = address.getAddress();
        }

        String subject = message.getSubject();

        String body = getTextFromMessage(message);
        return DetailedReceivedEmail.builder()
                .body(body)
                .receiverEmail(receiverEmail)
                .senderEmail(senderEmail)
                .subject(subject)
                .receivedDate(message.getReceivedDate())
                .build();
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

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.getContent() instanceof MimeMultipart mimeMultipart) {
            System.out.println("mime message");
            return getTextFromMimeMultipart2(mimeMultipart);
        }
        System.out.println("Plain text");
        return message.getContent().toString();
    }

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

    public String getTextFromMimeMultipart2(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {

                result = result + "\n" + bodyPart.getContent();
                break;

            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html; //org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart2((MimeMultipart) bodyPart.getContent());

            }
        }
        return result;
    }

}
