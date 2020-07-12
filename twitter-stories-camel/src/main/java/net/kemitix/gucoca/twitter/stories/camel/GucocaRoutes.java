package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.stories.*;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    @Inject PostingFrequency postingFrequency;
    @Inject StorySelector storySelector;

    @PropertyInject("gucoca.twitterstories.postfrequency")
    int postFrequency;
    @PropertyInject("gucoca.twitterstories.chancetopost")
    String changeToPost;
    @PropertyInject("gucoca.twitterstories.postdelay")
    long postDelay;

    @Override
    public void configure() {
        errorHandler(deadLetterChannel(SendEmail.SEND_ERROR));
        from(postingFrequency.startTimer(postFrequency))
                .routeId("main")
                .setHeader("Gucoca.TwitterStories.ChanceToPost", constant(changeToPost))
                .filter().method(postingFrequency, "shouldIRun")
                .setBody(exchange -> StoryContext.builder().build())
                .enrich(BroadcastHistory.LOAD_ENDPOINT)
                .enrich(Stories.LOAD_STORIES)
                .filter(simple("${body.stories.size} > 0"))
                .bean(storySelector, "select")
                .log("Story selected ${body.slug}")
                .enrich(Stories.ADD_STORY_CARD)
                .to(Stories.NOTIFY_SELECTION)
                .delay(postDelay)
                .to(TwitterStoryPublisher.PUBLISH)
        ;
    }

}
