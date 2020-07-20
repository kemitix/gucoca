package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.Story;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPost;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPublisher;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.inject.Inject;

/**
 * Posts the story to Twitter.
 */
class TwitterStoryPublisherRoute
    extends RouteBuilder
        implements TwitterStoryPublisher {

    @PropertyInject("gucoca.twitterstories.enabled")
    boolean twitterEnabled;
    @PropertyInject("gucoca.twitterstories.s3bucketname")
    String s3BucketName;

    @Inject
    TimelineComponentConfig timelineComponentConfig;

    @Inject
    TwitterStoryPost twitterStoryPost;

    @Override
    public void configure() {
        timelineComponentConfig.prepare(getContext());
        from("direct:Gucoca.TwitterStories.UnmarshalStory")
                .routeId("Gucoca.TwitterStories.UnmarshalStory")
                .unmarshal().json(JsonLibrary.Jackson, Story.class)
        ;

        from("direct:Gucoca.TwitterStories.isEnabled")
                .routeId("Gucoca.TwitterStories.isEnabled")
                .choice()
                .when(exchange -> twitterEnabled)
                .to("direct:Gucoca.TwitterStoryPublish.Publish.Enabled")
                .to(BroadcastHistory.UPDATE_ENDPOINT)
                .otherwise()
                .log("Not posting to twitter")
                .end()
        ;

        from("direct:Gucoca.TwitterStories.Post")
                .routeId("Gucoca.TwitterStories.Post")
                .setHeader("s3BucketName", constant(s3BucketName))
                .setHeader(STORY, simple("${body}"))
                .bean(twitterStoryPost,
                        "preparePost(${body}, ${header.s3BucketName})")
                .log("Posting to Twitter...")
                .to("twitter-timeline:USER")
                .log("Posted to Twitter")
                .setBody(header(STORY))
        ;
    }

}
