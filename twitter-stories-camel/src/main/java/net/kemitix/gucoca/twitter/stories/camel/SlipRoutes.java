package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.twitter.stories.PostingFrequency;
import net.kemitix.gucoca.twitter.stories.StoryContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

/**
 * Routing slip managed routes.
 */
public class SlipRoutes extends RouteBuilder {

    @Inject
    PostingFrequency postingFrequency;

    @PropertyInject("gucoca.twitterstories.delayqueue")
    String delayQueue;

    @PropertyInject("gucoca.twitterstories.postfrequency")
    int postFrequency;

    @Override
    public void configure() {
        from(postingFrequency.startTimer(postFrequency))
                .routeId("Gucoca.TwitterStories")
                .setBody(exchange -> StoryContext.empty())
                .setHeader("Gucoca.RoutingSlip",
                        simple("{{gucoca.twitterstories.slip.prepare}}"))
                .log("${header.[Gucoca.RoutingSlip]}")
                .routingSlip(header("Gucoca.RoutingSlip"))
        ;
        from(delayQueue)
                .routeId("Gucoca.TwitterStories.Publish")
                .log("Picking up delayed message...")
                .setHeader("Gucoca.RoutingSlip",
                        simple("{{gucoca.twitterstories.slip.publish}}"))
                .log("${header.[Gucoca.RoutingSlip]}")
                .routingSlip(header("Gucoca.RoutingSlip"))
        ;
    }
}
