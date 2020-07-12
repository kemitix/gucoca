package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.Story;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPublisher;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.timeline.TwitterTimelineComponent;
import twitter4j.StatusUpdate;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Posts the story to Twitter.
 */
class TwitterStoryPublisherRoute
    extends RouteBuilder
        implements TwitterStoryPublisher {

    private static final Random random = new Random();
    private static int maxTweetLength = 280;

    @PropertyInject("gucoca.twitterstories.enabled")
    boolean twitterEnabled;

    @PropertyInject("gucoca.twitterstories.token.access")
    String twitterAccessToken;

    @PropertyInject("gucoca.twitterstories.token.secret")
    String twitterAccessTokenSecret;

    @PropertyInject("gucoca.twitterstories.api.key")
    String twitterApiKey;

    @PropertyInject("gucoca.twitterstories.api.secret")
    String twitterApiSecretKey;

    @Override
    public void configure() {
        prepareTimelineComponent(getContext());
        from(PUBLISH)
                .routeId("twitter-story-publisher")
                .process(preparePost())
                .choice()
                .when(exchange -> twitterEnabled)
                .to("twitter-timeline:USER")
                .otherwise()
                .log("Not posting to twitter")
                .end()
                .transform(simple("${header.["+ STORY +"].slug}"))
                .to(BroadcastHistory.UPDATE_ENDPOINT);
    }

    private void prepareTimelineComponent(CamelContext camelContext) {
        TwitterTimelineComponent component =
                camelContext.getComponent("twitter-timeline",
                        TwitterTimelineComponent.class);
        component.setAccessToken(twitterAccessToken);
        component.setAccessTokenSecret(twitterAccessTokenSecret);
        component.setConsumerKey(twitterApiKey);
        component.setConsumerSecret(twitterApiSecretKey);
    }

    private Processor preparePost() {
        return exchange -> {
            Message in = exchange.getIn();
            Story story = in.getHeader(STORY, Story.class);
            String title = story.getTitle();
            String author = story.getAuthor();
            List<String> blurbs = story.getBlurb();
            String blurb = blurbs.get(random.nextInt(blurbs.size()));
            String url = story.getUrl();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Read %s by %s. %s %s",
                    title, author, blurb, url));
            List<String> hashtags = Arrays.asList("free", "fiction", "shortstory");
            hashtags.forEach(hashtag -> {
                if (sb.length() + 2 + hashtag.length() <= maxTweetLength) {
                    sb.append(String.format(" #%s", hashtag));
                }
            });
            InputStream cardStream = story.getStoryCardInputStream();
            StatusUpdate statusUpdate =
                    new StatusUpdate(sb.toString())
                            .media("story-card", cardStream);
            // can't use attachmentUrl as that is for links on twitter itself
            in.setBody(statusUpdate);
        };
    }
}
