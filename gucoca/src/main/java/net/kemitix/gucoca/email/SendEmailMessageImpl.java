package net.kemitix.gucoca.email;

import lombok.RequiredArgsConstructor;
import net.kemitix.gucoca.common.spi.SendEmailMessage;
import org.apache.camel.Message;
import org.apache.camel.component.aws.ses.SesConstants;

import java.util.List;

@RequiredArgsConstructor
class SendEmailMessageImpl implements SendEmailMessage {

    private final Message in;

    @Override
    public SendEmailMessage from(String from) {
        in.setHeader(SesConstants.FROM, from);
        return this;
    }

    @Override
    public SendEmailMessage to(List<String> to) {
        in.setHeader(SesConstants.TO, to);
        return this;
    }

    @Override
    public SendEmailMessage replyTo(List<String> replyTo) {
        in.setHeader(SesConstants.REPLY_TO_ADDRESSES, replyTo);
        return this;
    }

    @Override
    public SendEmailMessage subject(String subject) {
        in.setHeader(SesConstants.SUBJECT, subject);
        return this;
    }

    @Override
    public SendEmailMessage html() {
        in.setHeader(SesConstants.HTML_EMAIL, true);
        return this;
    }

    @Override
    public void body(String body) {
        in.setBody(body, String.class);
    }
}
