package net.kemitix.gucoca.camel.twitter;

import net.kemitix.gucoca.camel.history.BroadcastHistory;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.timeline.TwitterTimelineComponent;
import twitter4j.StatusUpdate;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Posts the story to Twitter.
 *
 * <p>Takes the {@link Story} from the {@link #STORY} together with an
 * image read from an {@link InputStream} in the {@link #STORYCARD}.</p>
 */
class TwitterStoryPublisherRoute
    extends RouteBuilder
        implements TwitterStoryPublisher {

    private static final Random random = new Random();
    private static int maxTweetLength = 280;

    @Inject GucocaConfig config;

    @Override
    public void configure() {
        prepareTimelineComponent(getContext());
        from(PUBLISH)
                .routeId("twitter-story-publisher")
                .process(preparePost())
                .choice()
                .when(exchange -> config.isTwitterEnabled())
                .to("twitter-timeline:USER")
                .otherwise()
                .log("Not posting to twitter")
                .end()
                .transform(simple("${header.["+ STORY +"].slug}"))
                .to(BroadcastHistory.UPDATE_ENDPOINT);
    }

    void prepareTimelineComponent(CamelContext camelContext) {
        TwitterTimelineComponent component =
                camelContext.getComponent("twitter-timeline",
                        TwitterTimelineComponent.class);
        component.setAccessToken(config.getTwitterAccessToken());
        component.setAccessTokenSecret(config.getTwitterAccessTokenSecret());
        component.setConsumerKey(config.getTwitterApiKey());
        component.setConsumerSecret(config.getTwitterApiSecretKey());
    }

    Processor preparePost() {
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
            InputStream cardStream = in.getHeader(STORYCARD, InputStream.class);
            StatusUpdate statusUpdate =
                    new StatusUpdate(sb.toString())
                            .media("story-card", cardStream);
            // can't use attachmentUrl as that is for links on twitter itself
            in.setBody(statusUpdate);
        };
    }
}
