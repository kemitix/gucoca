package net.kemitix.gucoca;

import net.kemitix.gucoca.twitter.PostingFrequency;
import net.kemitix.gucoca.twitter.StoryContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

/**
 * Routing slip managed routes.
 */
public class SlipRoutes extends RouteBuilder {

    private static final String ROUTING_SLIP = "Gucoca.RoutingSlip";

    @Inject
    PostingFrequency postingFrequency;

    @Inject
    FilteredSlip filteredSlip;

    @PropertyInject("gucoca.twitterstories.delayqueue")
    String delayQueue;

    @PropertyInject("gucoca.twitterstories.postfrequency")
    int postFrequency;

    @Override
    public void configure() {
        from(postingFrequency.startTimer(postFrequency))
                .routeId("Gucoca.TwitterStories")
                .setBody(exchange -> StoryContext.empty())
                .setHeader(ROUTING_SLIP, bean(filteredSlip,
                        "filter({{gucoca.twitterstories.slip.prepare}})"))
                .log("${header.[Gucoca.RoutingSlip]}")
                .routingSlip(header(ROUTING_SLIP))
        ;
        from(delayQueue)
                .routeId("Gucoca.TwitterStories.Publish")
                .log("Picking up delayed message...")
                .setHeader(ROUTING_SLIP, bean(filteredSlip,
                        "filter({{gucoca.twitterstories.slip.publish}})"))
                .log("${header.[Gucoca.RoutingSlip]}")
                .routingSlip(header(ROUTING_SLIP))
        ;
    }
}
