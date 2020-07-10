package net.kemitix.gucoca.camel.email;

import net.kemitix.gucoca.camel.aws.AwsSES;
import net.kemitix.gucoca.spi.GucocaConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ses.SesConstants;

import javax.inject.Inject;
import java.util.Collections;

class SendEmailRoute
        extends RouteBuilder
        implements SendEmail{

    @Inject GucocaConfig config;
    @Inject AwsSES awsSES;

    @Override
    public void configure() {
        from(SEND)
                .routeId("send-email")
                .log(String.format(
                        "Sending email to ${header.[%s]}: ${header.[%s]}",
                        SesConstants.TO, SesConstants.SUBJECT))
                .to(awsSES.send("sender"));

        from(SEND_ERROR)
                .routeId("send-email-error")
                .setBody(header(Exchange.EXCEPTION_CAUGHT))//FIXME: not much info in this
                .setHeader(SesConstants.TO, constant(Collections.singletonList(
                        config.getNotificationRecipient())))
                .setHeader(SesConstants.FROM, constant(
                        config.getEmailSender()))
                .setHeader(SesConstants.SUBJECT, constant(
                        "ERROR"))
                .to(awsSES.send("error"));
    }

    @Override
    public SendEmailMessage message(Message in) {
        return new SendEmailMessageImpl(in);
    }

}
