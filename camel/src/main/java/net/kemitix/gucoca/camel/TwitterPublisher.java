package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.BuilderSupport;
import org.apache.camel.component.twitter.timeline.TwitterTimelineComponent;
import twitter4j.StatusUpdate;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class TwitterPublisher {

    private static final Random random = new Random();
    private static int maxTweetLength = 280;

    @Inject GucocaConfig config;

    Processor preparePost() {
        return exchange -> {
            Message in = exchange.getIn();
            Story story = in.getHeader(Headers.STORY_SELECTED, Story.class);
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
            InputStream cardStream = in.getHeader(Headers.STORY_CARD_INPUTSTREAM, InputStream.class);
            StatusUpdate statusUpdate =
                    new StatusUpdate(sb.toString())
                            .media("story-card", cardStream);
            // can't use attachmentUrl as that is for links on twitter itself
            in.setBody(statusUpdate);
        };
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
}
