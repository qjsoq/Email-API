package com.example.emailapp.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EmailConfiguration {

    GMAIL("gmail", "smtp.gmail.com", 587, false, true),
    OUTLOOK("outlook", "smtp-mail.outlook.com", 587, false, true),
    UKRNET("ukr", "smtp.ukr.net", 465, false, true),
    YAHOO("yahoo", "smtp.mail.yahoo.com", 465, true, false);

    private final String domain;
    private final String host;
    private final int port;
    private final boolean useSsl;
    private final boolean useTls;

    public String getDomainName() {
        return domain;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public boolean isUseTls() {
        return useTls;
    }
}
