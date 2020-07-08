package net.kemitix.gucoca.camel.email;

import net.kemitix.gucoca.camel.aws.AwsSES;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ses.SesConstants;

import javax.inject.Inject;

class SendEmailRoute
        extends RouteBuilder
        implements SendEmail{

    @Inject AwsSES awsSES;

    @Override
    public void configure() {
        from(SEND)
                .log(String.format(
                        "Sending email to ${header.[%s]}: ${header.[%s]}",
                        SesConstants.TO, SesConstants.SUBJECT))
                .to(awsSES.send("sender"));
    }

    @Override
    public SendEmailMessage message(Message in) {
        return new SendEmailMessageImpl(in);
    }

}
