package net.kemitix.gucoca.camel.stories;

import net.kemitix.gucoca.camel.email.SendEmail;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;
import java.net.InetAddress;

class StoriesRoutes
        extends RouteBuilder
        implements Stories {

    @Inject GucocaConfig config;
    @Inject SendEmail sendEmail;
    @Inject StoryLoader storyLoader;

    @Override
    public void configure() {

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

        from(NOTIFY_SELECTION)
                .process(prepareNotificationEmail())
                .to(SendEmail.SEND)
        ;
    }

    private Processor prepareNotificationEmail() {
        return exchange -> {
            Message in = exchange.getIn();
            Story story = in.getBody(Story.class);
            sendEmail
                    .message(in)
                    .from(config.getEmailSender())
                    .to(config.getNotificationRecipient())
                    .subject(String.format("Story Selected: %s by %s",
                            story.getTitle(),
                            story.getAuthor()
                    ))
                    .html()
                    .body(String.format("<h1>Selected: %s by %s</h1>" +
                                    "<p>This story has been selected to be promoted " +
                                    "on Twitter.</p>" +
                                    "<p>Sent from: %s</p>",
                            story.getTitle(), story.getAuthor(),
                            InetAddress.getLocalHost().getHostName()));
        };
    }
}
