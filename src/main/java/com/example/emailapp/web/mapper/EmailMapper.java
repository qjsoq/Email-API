package com.example.emailapp.web.mapper;

import com.example.emailapp.domain.Email;
import com.example.emailapp.web.dto.email.EmailCreationDto;
import com.example.emailapp.web.dto.email.EmailDto;
import com.example.emailapp.web.dto.email.ReceivedEmail;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface EmailMapper {
    Email toEmail(EmailCreationDto emailCreationDto);

    EmailDto toDto(Email email);

    @Mapping(source = "from", target = "sender", qualifiedByName = "extractSender")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "receivedDate", target = "receivedDate")
    ReceivedEmail toReceivedEmail(Message message) throws MessagingException;

    @Named("extractSender")
    static String extractSender(Address[] addresses) {
        if (addresses != null && addresses.length > 0) {
            return ((InternetAddress) addresses[0]).getAddress();
        }
        return null;
    }
}
