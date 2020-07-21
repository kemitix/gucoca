package net.kemitix.gucoca;

import net.kemitix.gucoca.common.spi.SendEmail;
import net.kemitix.gucoca.twitter.PostingFrequency;
import net.kemitix.gucoca.twitter.StorySelector;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    private static final String CHANCE_TO_POST = "Gucoca.TwitterStories.ChanceToPost";
    @Inject
    PostingFrequency postingFrequency;
    @Inject
    StorySelector storySelector;

    @PropertyInject("gucoca.twitterstories.chancetopost")
    String changeToPost;

    @PropertyInject("gucoca.twitterstories.postdelay")
    long postDelay;

    @PropertyInject("gucoca.twitterstories.delayqueue")
    String delayQueue;

    @Override
    public void configure() {
        errorHandler(deadLetterChannel(SendEmail.SEND_ERROR));

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
                .bean(storySelector, "stories")
                .choice()
                .when(simple("${body.size} == 0"))
                .log("No Stories available to select")
                .stop()
                .end()
                .bean(storySelector, "select")
                .log("Story selected ${body.slug}")
        ;

        from("direct:Gucoca.TwitterStories.Delay")
                .routeId("Gucoca.TwitterStories.Delay")
                .log(String.format("Sending to %s", delayQueue))
                .marshal().json(true)
                .setHeader("AMQ_SCHEDULED_DELAY", constant(postDelay))
                .to(delayQueue)
        ;
    }

}
