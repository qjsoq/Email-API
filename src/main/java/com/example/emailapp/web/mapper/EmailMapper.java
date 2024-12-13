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
    @Named("extractPersonal")
    static String extractPersonal(Address[] addresses) {
        return getAddressPart(addresses);
    }

    private static String getAddressPart(Address[] addresses) {
        if (addresses != null && addresses.length > 0 &&
                addresses[0] instanceof InternetAddress internetAddress) {
            return internetAddress.getPersonal();
        }
        return null;
    }

    Email toEmail(EmailCreationDto emailCreationDto);

    EmailDto toDto(Email email);

    @Mapping(source = "from", target = "personal", qualifiedByName = "extractPersonal")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "receivedDate", target = "receivedDate")
    @Mapping(target = "msgnum", expression = "java(message.getMessageNumber())")
    ReceivedEmail toReceivedEmail(Message message) throws MessagingException;
}
