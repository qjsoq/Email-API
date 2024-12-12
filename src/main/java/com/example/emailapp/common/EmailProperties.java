package com.example.emailapp.common;

import com.example.emailapp.utils.EmailConfiguration;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProperties {
    private final EmailConfiguration ukrNetConfig = EmailConfiguration.UKRNET;
    private final EmailConfiguration gmailConfig = EmailConfiguration.GMAIL;

    @Bean(name = "ukr-properties")
    public Properties getUkrNetProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", ukrNetConfig.getHost());
        props.put("mail.smtp.port", ukrNetConfig.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", ukrNetConfig.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return props;
    }

    @Bean(name = "ukr-imap-properties")
    public Properties getImapUkrNetProperties() {
        return getDefaultProperties(ukrNetConfig);
    }
    @Bean(name = "gmail-imap-properties")
    public Properties getImapGmailProperties() {
        Properties props = getDefaultProperties(gmailConfig);
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        return props;
    }

    private Properties getDefaultProperties(EmailConfiguration properties) {
        Properties props = new Properties();
        props.put("mail.imap.host", properties.getImapHost());
        props.put("mail.imaps.ssl.trust", properties.getImapHost());
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", properties.isUseTls());
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");
        return props;
    }

    @Bean(name = "gmail-properties")
    public Properties GetGmailProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", gmailConfig.getPort());
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", gmailConfig.isUseTls());
        return props;
    }
}
