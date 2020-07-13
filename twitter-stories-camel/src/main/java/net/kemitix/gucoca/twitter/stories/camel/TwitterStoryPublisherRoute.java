package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPost;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPublisher;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

/**
 * Posts the story to Twitter.
 */
class TwitterStoryPublisherRoute
    extends RouteBuilder
        implements TwitterStoryPublisher {

    @PropertyInject("gucoca.twitterstories.enabled")
    boolean twitterEnabled;

    @Inject
    TimelineComponentConfig timelineComponentConfig;

    @Inject
    TwitterStoryPost twitterStoryPost;

    @Override
    public void configure() {
        timelineComponentConfig.prepare(getContext());
        from(PUBLISH)
                .routeId("twitter-story-publisher")
                .choice()
                .when(exchange -> twitterEnabled)
                .to("direct:Gucoca.TwitterStoryPublish.Publish.Enabled")
                .otherwise()
                .log("Not posting to twitter")
                .end()
                .to(BroadcastHistory.UPDATE_ENDPOINT)
        ;

        from("direct:Gucoca.TwitterStoryPublish.Publish.Enabled")
                .routeId("twitter-story-publisher-enabled")
                .bean(twitterStoryPost, String.format(
                        "preparePost(${header.[%s]})", STORY))
                .to("twitter-timeline:USER")
        ;
    }

}
