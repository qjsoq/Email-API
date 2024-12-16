package com.example.emailapp.service.impl;

import com.example.emailapp.repository.MailBoxRepository;
import com.example.emailapp.service.ImapService;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImapServiceImpl implements ImapService {
    private final List<Properties> listOfImapProperties;
    private final MailBoxRepository mailBoxRepository;
    private Properties imapProperties;

    public static Folder getFolderFromStore(Store store, String folderName, int type) throws
            MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(type);
        return folder;
    }

    public static SearchTerm getMessagesSearchTerm() {
        Date yesterdayDate = new Date(new Date().getTime() - (1000L * 60 * 60 * 24 * 7));
        return new ReceivedDateTerm(ComparisonTerm.GT, yesterdayDate);
    }

    private static FetchProfile getFetchProfile() {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(UIDFolder.FetchProfileItem.ENVELOPE);
        fetchProfile.add(UIDFolder.FetchProfileItem.CONTENT_INFO);
        fetchProfile.add("X-mailer");
        return fetchProfile;
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

    private Store getImapStore(String account) throws Exception {
        var mailbox = mailBoxRepository.findByEmailAddress(account).orElseThrow(
                () -> new RuntimeException("Mailbox not found for account: " + account));
        var imapConfig = mailbox.getEmailConfiguration();
        setProperties(imapConfig.getImapHost());
        Session session = Session.getInstance(imapProperties);
        Store store = session.getStore("imap");
        store.connect(imapConfig.getImapHost(), account, mailbox.getAccessSmtp());
        return store;
    }

    private void setProperties(String domainName) {
        this.imapProperties = listOfImapProperties.stream()
                .filter(properties -> {
                    String host = properties.getProperty("mail.imap.host");
                    return host != null && host.contains(domainName);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No matching Properties bean found for domain: " + domainName));
    }


    @Override
    public Message[] getEmails(String account, String folderName) throws Exception {

        Store store = getImapStore(account);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_ONLY);
        int messageCount = folder.getMessageCount();
        if (messageCount == 0) {
            return new Message[0];
        }
        int start = Math.max(1, messageCount - 14);
        Message[] messages = folder.getMessages(start, messageCount);
        folder.fetch(messages, getFetchProfile());
        closeFolder(folder);
        closeStore(store);
        return messages;
    }

    @Override
    public Message getEmail(String account, String folderName, int msgnum) throws Exception {
        Store store = getImapStore(account);
        Folder folder = getFolderFromStore(store, folderName, Folder.READ_ONLY);
        return folder.getMessage(msgnum);
    }

    @Override
    public void moveEmail(String account, String sourceFolder, String destinationFolder,
                          int msgnum) throws Exception {
        Store store = getImapStore(account);
        Folder source = getFolderFromStore(store, sourceFolder, Folder.READ_WRITE);
        Message[] messages = new Message[]{source.getMessage(msgnum)};

        Folder destination = getFolderFromStore(store, destinationFolder, Folder.READ_WRITE);
        source.copyMessages(messages, destination);
        source.close();
        destination.close();
    }

    @Override
    public boolean createFolder(String folderName, String account) throws Exception {
        Store store = getImapStore(account);
        Folder newFolder = store.getFolder(folderName);
        if (!newFolder.exists()) {
            if (newFolder.create(Folder.HOLDS_MESSAGES)) {
                newFolder.setSubscribed(true);
                return true;
            }
        }
        closeFolder(newFolder);
        closeStore(store);
        return false;
    }
}
