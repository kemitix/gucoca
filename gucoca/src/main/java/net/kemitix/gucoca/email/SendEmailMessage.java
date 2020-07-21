package net.kemitix.gucoca.email;

import java.util.Collections;
import java.util.List;

public interface SendEmailMessage {

    SendEmailMessage from(String from);
    SendEmailMessage to(List<String> to);
    default SendEmailMessage to(String to) {
        return to(Collections.singletonList(to));
    }
    SendEmailMessage replyTo(List<String> replyTo);
    default SendEmailMessage replyTo(String replyTo) {
        return replyTo(Collections.singletonList(replyTo));
    }
    SendEmailMessage subject(String subject);
    SendEmailMessage html();
    void body(String body);

}
