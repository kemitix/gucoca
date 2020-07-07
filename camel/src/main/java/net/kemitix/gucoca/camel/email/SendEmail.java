package net.kemitix.gucoca.camel.email;

import org.apache.camel.Message;

public interface SendEmail {

    String SEND = "direct:Gucoca.SendEmail.Send";

    SendEmailMessage message(Message in);

    String send(String sender);

}
