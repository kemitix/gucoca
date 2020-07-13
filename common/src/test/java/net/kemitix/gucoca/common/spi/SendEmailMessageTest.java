package net.kemitix.gucoca.common.spi;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SendEmailMessageTest
        implements WithAssertions {

    @Test
    @DisplayName("default to(String) wrap String in a List")
    public void defaultToWrapsStringInList() {
        //given
        var message = new TestSendEmailMessage();
        var recipient = "recipient";
        //when
        message.to(recipient);
        //then
        assertThat(message.recipients)
                .containsExactly(recipient);
    }

    @Test
    @DisplayName("default replyTo(String) wraps String in a List")
    public void defaultReplyToWrapsStringInList() {
        //given
        var message = new TestSendEmailMessage();
        var replyTo = "replier";
        //when
        message.replyTo(replyTo);
        //then
        assertThat(message.replyTo)
                .containsExactly(replyTo);
    }

    static class TestSendEmailMessage implements SendEmailMessage {

        List<String> recipients = new ArrayList<>();
        List<String> replyTo = new ArrayList<>();

        @Override
        public SendEmailMessage from(String from) {
            return this;
        }

        @Override
        public SendEmailMessage to(List<String> to) {
            recipients.addAll(to);
            return this;
        }

        @Override
        public SendEmailMessage replyTo(List<String> replyTo) {
            this.replyTo.addAll(replyTo);
            return this;
        }

        @Override
        public SendEmailMessage subject(String subject) {
            return null;
        }

        @Override
        public SendEmailMessage html() {
            return null;
        }

        @Override
        public void body(String body) {

        }
    }

}