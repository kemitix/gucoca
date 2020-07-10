package net.kemitix.gucoca.camel.email;

import org.apache.camel.Message;

public interface SendEmail {

    String SEND = "direct:Gucoca.SendEmail.Send";
    String SEND_ERROR = "direct:Gucoca.SendEmail.Send.Error";

    SendEmailMessage message(Message in);

}
