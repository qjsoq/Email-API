package com.example.emailapp.common;

import com.example.emailapp.utils.EmailConfiguration;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProperties {
    @Bean(name = "ukr-properties")
    public Properties getUkrNetSession() {
        var ukrNetConfig = EmailConfiguration.UKRNET;
        var properties = getDefualtProperties(ukrNetConfig.getPort(), ukrNetConfig.isUseTls());
        properties.put("mail.smtp.host", ukrNetConfig.getHost());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", ukrNetConfig.getPort());
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        return properties;
    }
    private Properties getDefualtProperties(int port, boolean isUseTls) {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", isUseTls);
        return props;
    }
}
