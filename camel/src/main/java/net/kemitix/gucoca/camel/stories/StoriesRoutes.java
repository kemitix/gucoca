package net.kemitix.gucoca.camel.stories;

import net.kemitix.gucoca.camel.email.SendEmail;
import net.kemitix.gucoca.spi.GucocaConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.component.aws.ses.SesConstants;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

class StoriesRoutes
        extends RouteBuilder
        implements Stories {

    @Inject GucocaConfig config;
    @Inject SendEmail sendEmail;
    @Inject StoryLoader storyLoader;

    @Override
    public void configure() throws UnknownHostException {

        from(LOAD_STORIES)
                .routeId("load-stories")
                .setBody(exchange -> exchange.getIn()
                        .getBody(StoryContext.class)
                        .withIssues(storyLoader.load()))
                .log("Loaded Issues ${body.issues.size}")
                .log("Loaded Stories ${body.stories.size}")
        ;

        from(ADD_STORY_CARD)
                .routeId("add-story-card")
                .bean(storyLoader, "addStoryCard")
        ;

        ValueBuilder sender = constant(config.getEmailSender());
        ValueBuilder recipient = constant(Collections.singletonList(
                config.getNotificationRecipient()));
        SimpleBuilder subject = simple(
                "Story Selected: ${body.title} by ${body.author}");
        ValueBuilder hostname = constant(
                InetAddress.getLocalHost().getHostName());
        from(NOTIFY_SELECTION)
                .setHeader(SesConstants.FROM, sender)
                .setHeader(SesConstants.TO, recipient)
                .setHeader(SesConstants.HTML_EMAIL, constant(true))
                .setHeader(SesConstants.SUBJECT, subject)
                .setHeader("Hostname", hostname)
                .to("velocity://gucoca/stories/selected-notice.vm")
                .to(SendEmail.SEND)
        ;
    }

}
