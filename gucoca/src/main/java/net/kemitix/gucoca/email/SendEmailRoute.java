package net.kemitix.gucoca.email;

import net.kemitix.gucoca.aws.AwsSES;
import net.kemitix.gucoca.aws.AwsSesConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ses.SesConstants;

import javax.inject.Inject;
import java.util.Collections;

import static org.apache.camel.builder.Builder.bean;

class SendEmailRoute
        extends RouteBuilder
        implements SendEmail {

    @Inject AwsSesConfig config;
    @Inject AwsSES awsSES;

    @Override
    public void configure() {
        from(SEND)
                .routeId("Gucoca.SendEmail.Send")
                .log(String.format(
                        "Sending email to ${header.[%s]}: ${header.[%s]}",
                        SesConstants.TO, SesConstants.SUBJECT))
                .to(awsSES.send("sender", config));

        from(SEND_ERROR)
                .routeId("Gucoca.SendEmail.Send.Error")
                .setBody(bean(ExceptionStackTrace.class, String.format(
                        "generate(${header.[%s]})", Exchange.EXCEPTION_CAUGHT)))
                .log("${body}")
                .setHeader(SesConstants.TO, constant(Collections.singletonList(
                        config.getNotificationRecipient())))
                .setHeader(SesConstants.FROM, constant(
                        config.getEmailSender()))
                .setHeader(SesConstants.SUBJECT, simple(String.format(
                        "ERROR: ${header.[%s]}", Exchange.EXCEPTION_CAUGHT)))
                .to(SEND);
    }

}
