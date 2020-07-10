package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.stories.*;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    @Inject
    TwitterStoriesConfig config;
    @Inject PostingFrequency postingFrequency;
    @Inject StorySelector storySelector;

    @Override
    public void configure() {
        errorHandler(deadLetterChannel(SendEmail.SEND_ERROR));
        from(postingFrequency.startTimer())
                .routeId("main")
                .filter().method(postingFrequency, "shouldIRun")
                .setBody(exchange -> StoryContext.builder().build())
                .enrich(BroadcastHistory.LOAD_ENDPOINT)
                .enrich(Stories.LOAD_STORIES)
                .filter(simple("${body.stories.size} > 0"))
                .bean(storySelector, "select")
                .log("Story selected ${body.slug}")
                .enrich(Stories.ADD_STORY_CARD)
                .to(Stories.NOTIFY_SELECTION)
                .delay(config.getTwitterDelayMillis())
                .to(TwitterStoryPublisher.PUBLISH)
        ;
    }

}
