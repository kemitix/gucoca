package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.stories.*;
import org.apache.camel.builder.RouteBuilder;

import java.util.Arrays;

import static org.apache.camel.builder.Builder.bean;

public class GucocaRoutes extends RouteBuilder {

    @Override
    public void configure() {
        errorHandler(deadLetterChannel(SendEmail.SEND_ERROR));
        try {
            fromF("timer:twitter-stories-start?period=%d", 60 * 1000)
                    .routeId("main")
                    .log("log:main")
                    .filter().method(PostingFrequency.class, "shouldIRun")
                    .log("pre")
                    .setBody(exchange -> StoryContext.builder().build())
                    .log("alpha")
                    .enrich(BroadcastHistory.LOAD_ENDPOINT)
                    .log("gamma")
                    .enrich(Stories.LOAD_STORIES)
                    .log("delta")
                    .filter(simple("${body.stories.size} > 0"))
                    .log("omega")
                    .bean(StorySelector.class, "select")
                    .log("Story selected ${body.slug}")
                    .enrich(Stories.ADD_STORY_CARD)
                    .to(Stories.NOTIFY_SELECTION)
                    .delay(bean(TwitterStoriesConfig.class, "getTwitterDelayMillis"))
                    .to(TwitterStoryPublisher.PUBLISH)
            ;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Arrays.asList(npe.getSuppressed())
                    .forEach(e -> log.error(e.getMessage()));
        }
    }

}
