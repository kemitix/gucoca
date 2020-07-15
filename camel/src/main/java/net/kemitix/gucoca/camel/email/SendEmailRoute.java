package net.kemitix.gucoca.camel.email;

import net.kemitix.gucoca.common.spi.AwsSES;
import net.kemitix.gucoca.common.spi.AwsSesConfig;
import net.kemitix.gucoca.common.spi.SendEmail;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ses.SesConstants;

import javax.inject.Inject;
import java.util.Collections;

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
                .setBody(header(Exchange.EXCEPTION_CAUGHT))//FIXME: not much info in this
                .setHeader(SesConstants.TO, constant(Collections.singletonList(
                        config.getNotificationRecipient())))
                .setHeader(SesConstants.FROM, constant(
                        config.getEmailSender()))
                .setHeader(SesConstants.SUBJECT, constant(
                        "ERROR"))
                .to(SEND);
    }

}
