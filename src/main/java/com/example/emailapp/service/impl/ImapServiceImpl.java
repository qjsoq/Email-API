package com.example.emailapp.service.impl;

import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.ImapService;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImapServiceImpl implements ImapService {
    private final MailBoxRepository mailBoxRepository;
    @Override
    public Message[] getEmails(String account, String folderName) throws Exception {
        EMAIL = account;
        var mailbox = mailBoxRepository.findByEmailAddress(account);
        APP_PASSWORD = mailbox.get().getAccessSmtp();
        Store store = getImapStore();
        Folder folder = getFolderFromStore(store, folderName);
        Message[] messages = folder.search(getMessagesSearchTerm());
        folder.fetch(messages, getFetchProfile());
        for (int i = 0; i < messages.length; i++) {
            printMessage(messages[i]);
        }
        closeFolder(folder);
        closeStore(store);
        return messages;
    }
    private static String EMAIL;
    private static String APP_PASSWORD;

    private static Store getImapStore() throws Exception {
        Session session = Session.getInstance(getImapProperties());
        Store store = session.getStore("imaps");
        store.connect("imap.ukr.net", EMAIL, APP_PASSWORD);
        System.out.println(store.getDefaultFolder());
        return store;
    }

    private static Properties getImapProperties() {
        Properties props = new Properties();
        props.put("mail.imaps.host", "imap.ukr.net");
        props.put("mail.imaps.ssl.trust", "imap.ukr.net");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");
        return props;
    }

    private static Folder getFolderFromStore(Store store, String folderName) throws
            MessagingException {
        Folder[] folders = store.getDefaultFolder().list("*");
        for (Folder folder : folders) {
            System.out.println("Folder: " + folder.getFullName());
        }
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private static SearchTerm getMessagesSearchTerm() {
        Date yesterdayDate = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        return new ReceivedDateTerm(ComparisonTerm.GT, yesterdayDate);
    }

    private static FetchProfile getFetchProfile() {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(UIDFolder.FetchProfileItem.ENVELOPE);
        fetchProfile.add(UIDFolder.FetchProfileItem.CONTENT_INFO);
        fetchProfile.add("X-mailer");
        return fetchProfile;
    }

    private static void printMessage(Message message) throws MessagingException, IOException {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("RECEIVED ON: ").append(message.getReceivedDate()).append("\n");

        Address[] addressesFrom = message.getFrom();
        String from = addressesFrom != null ? ((InternetAddress) addressesFrom[0]).getAddress() : null;
        messageBuilder.append("FROM: ").append(from).append("\n");

        messageBuilder.append("SUBJECT: ").append(message.getSubject()).append("\n");

        StringBuilder textCollector = new StringBuilder();
        collectTextFromMessage(textCollector, message);
        messageBuilder.append("TEXT: ").append(textCollector.toString()).append("\n");

        System.out.println(messageBuilder.toString());
    }

    private static void collectTextFromMessage(StringBuilder textCollector, Part part)
            throws MessagingException, IOException {
        if (part.isMimeType("text/plain")) {
            textCollector.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*") && part.getContent() instanceof Multipart) {
            Multipart multiPart = (Multipart) part.getContent();
            for (int i = 0; i < multiPart.getCount(); i++) {
                collectTextFromMessage(textCollector, multiPart.getBodyPart(i));
            }
        }
    }

    private static void closeFolder(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeStore(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
