package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.stories.Stories;
import net.kemitix.gucoca.twitter.stories.StoryContext;
import net.kemitix.gucoca.twitter.stories.StoryLoader;
import org.apache.camel.PropertyInject;
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

    @Inject
    StoryLoader storyLoader;
    @PropertyInject("gucoca.twitterstories.email.sender")
    String emailSender;
    @PropertyInject("gucoca.twitterstories.email.recipient")
    String notificationRecipient;

    @PropertyInject("gucoca.twitterstories.s3bucketname")
    String s3BucketName;
    @PropertyInject("gucoca.twitterstories.s3bucketprefix")
    String s3BucketPrefix;
    @PropertyInject("gucoca.twitterstories.issuefilename")
    String issueFilename;

    @Override
    public void configure() throws UnknownHostException {

        from(LOAD_STORIES)
                .routeId("load-stories")
                .setBody(exchange -> exchange.getIn()
                        .getBody(StoryContext.class)
                        .withIssues(storyLoader.load(s3BucketName, s3BucketPrefix, issueFilename)))
                .log("Loaded Issues ${body.issues.size}")
                .log("Loaded Stories ${body.stories.size}")
        ;

        from(ADD_STORY_CARD)
                .routeId("add-story-card")
                .bean(storyLoader, "addStoryCard(${body}, {{gucoca.twitterstories.s3bucketname}})")
        ;

        ValueBuilder sender = constant(emailSender);
        ValueBuilder recipient = constant(Collections.singletonList(
                notificationRecipient));
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
