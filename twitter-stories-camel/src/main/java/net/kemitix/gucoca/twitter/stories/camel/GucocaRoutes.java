package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.stories.*;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    private static final String CHANCE_TO_POST = "Gucoca.TwitterStories.ChanceToPost";
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
                .routeId("Gucoca.TwitterStories")
                .setBody(exchange -> StoryContext.empty())
                .setHeader("Gucoca.RoutingSlip",
                        simple("{{gucoca.twitterstories.routingslip}}"))
                .log("${header.[Gucoca.RoutingSlip]}")
                .routingSlip(header("Gucoca.RoutingSlip"))
        ;

        from("direct:Gucoca.TwitterStories.Chance")
                .routeId("Gucoca.TwitterStories.Chance")
                .setHeader(CHANCE_TO_POST, constant(changeToPost))
                .choice()
                .when(method(postingFrequency, String.format(
                        "shouldIRun(${header.[%s]})", CHANCE_TO_POST)))
                .otherwise().stop()
                .end()
                .log("Chance - OKAY - running...")
        ;

        from("direct:Gucoca.TwitterStories.StoriesFound")
                .routeId("Gucoca.TwitterStories.StoriesFound")
                .choice()
                .when(simple("${body.stories.size} > 0"))
                .otherwise().stop()
                .end()
                .log("Found ${body.stories.size} stories")
        ;

        from("direct:Gucoca.TwitterStories.SelectStory")
                .routeId("Gucoca.TwitterStories.SelectStory")
                .bean(storySelector)
                .log("Story selected ${body.slug}")
        ;

        from("direct:Gucoca.TwitterStories.Delay")
                .routeId("Gucoca.TwitterStories.Delay")
                .log("Delaying: " + postDelay + " ms")
                .delay(postDelay)
                .log("Delay over")
        ;
    }

}
