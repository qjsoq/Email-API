package com.example.emailapp.utils;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String token){
        return "Hello " + name + "\n\nYour new account hase been created, to verify if it click the link below.\n\n" +
                getVerificationURL(host, token) + "\n\n The support team";
    }

    public static String getVerificationURL(String host, String token) {
        return host + "/api/users?token=" + token;
    }
}
