package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.camel.history.BroadcastHistory;
import net.kemitix.gucoca.camel.stories.Stories;
import net.kemitix.gucoca.camel.stories.StoryContext;
import net.kemitix.gucoca.camel.stories.StorySelector;
import net.kemitix.gucoca.camel.twitter.TwitterStoryPublisher;
import net.kemitix.gucoca.spi.GucocaConfig;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    @Inject GucocaConfig config;
    @Inject PostingFrequency postingFrequency;
    @Inject StorySelector storySelector;

    @Override
    public void configure() {
        from(postingFrequency.startTimer())
                .routeId("main")
                .filter(postingFrequency.shouldIRun())
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
