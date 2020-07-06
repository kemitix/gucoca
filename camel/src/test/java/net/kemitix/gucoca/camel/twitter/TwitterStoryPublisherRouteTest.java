package net.kemitix.gucoca.camel.twitter;

import net.kemitix.gucoca.camel.history.BroadcastHistory;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.StatusUpdate;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class TwitterStoryPublisherRouteTest
    extends CamelTestSupport
        implements WithAssertions {

    private final GucocaConfig config = new GucocaConfig();

    private final InputStream inputStream;

    TwitterStoryPublisherRouteTest(@Mock InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Test
    @DisplayName("Tweets a Story")
    public void tweetsStory() throws InterruptedException {
        //given
        MockEndpoint mock = getMockEndpoint("mock:twitter");
        mock.expectedMessageCount(1);

        config.setTwitterEnabled(true);

        Story story = new Story();
        story.setTitle("title");
        story.setAuthor("author");
        story.setUrl("URL");
        story.setBlurb(Collections.singletonList("blurb"));

        Map<String, Object> headers = new HashMap<>();
        headers.put(TwitterStoryPublisher.STORY, story);
        headers.put(TwitterStoryPublisher.STORYCARD, inputStream);
        Object body = null; // not used

        //when
        template.sendBodyAndHeaders(TwitterStoryPublisher.PUBLISH, body, headers);

        //then
        assertMockEndpointsSatisfied();

        List<Exchange> receivedExchanges = mock.getReceivedExchanges();
        assertThat(receivedExchanges).hasSize(1);
        Message in = receivedExchanges.get(0).getIn();
        StatusUpdate statusUpdate = in.getBody(StatusUpdate.class);
        assertThat(statusUpdate.getStatus()).isEqualTo(
                "Read title by author. blurb URL #free #fiction #shortstory"
        );
    }

    @Test
    @DisplayName("When disable don't tweet stosy")
    public void whenDisabledDoNoTweetStory() throws InterruptedException {
        //given
        MockEndpoint mock = getMockEndpoint("mock:twitter");
        mock.expectedMessageCount(0);

        config.setTwitterEnabled(false);

        Story story = new Story();
        story.setTitle("title");
        story.setAuthor("author");
        story.setUrl("URL");
        story.setBlurb(Collections.singletonList("blurb"));

        Map<String, Object> headers = new HashMap<>();
        headers.put(TwitterStoryPublisher.STORY, story);
        headers.put(TwitterStoryPublisher.STORYCARD, inputStream);
        Object body = null; // not used

        //when
        template.sendBodyAndHeaders(TwitterStoryPublisher.PUBLISH, body, headers);

        //then
        assertMockEndpointsSatisfied(2, TimeUnit.SECONDS);

        List<Exchange> receivedExchanges = mock.getReceivedExchanges();
        assertThat(receivedExchanges).isEmpty();
    }

    @Test
    @DisplayName("Tweets drops tags when no space")
    public void tweetDropsTagsWhenNoSpace() throws InterruptedException {
        //given
        MockEndpoint mock = getMockEndpoint("mock:twitter");
        mock.expectedMessageCount(1);

        config.setTwitterEnabled(true);

        Story story = new Story();
        story.setTitle("title title title title title title title title title title title");
        story.setAuthor("author author author author author author author author author");
        story.setUrl("URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL");
        story.setBlurb(Collections.singletonList("blurb blurb blurb blurb blurb blurb blurb blurb"));

        Map<String, Object> headers = new HashMap<>();
        headers.put(TwitterStoryPublisher.STORY, story);
        headers.put(TwitterStoryPublisher.STORYCARD, inputStream);
        Object body = null; // not used

        //when
        template.sendBodyAndHeaders(TwitterStoryPublisher.PUBLISH, body, headers);

        //then
        assertMockEndpointsSatisfied();

        List<Exchange> receivedExchanges = mock.getReceivedExchanges();
        assertThat(receivedExchanges).hasSize(1);
        Message in = receivedExchanges.get(0).getIn();
        StatusUpdate statusUpdate = in.getBody(StatusUpdate.class);
        assertThat(statusUpdate.getStatus().length()).isLessThanOrEqualTo(280);
        assertThat(statusUpdate.getStatus()).endsWith(" URL #free #fiction");
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        RouteBuilder[] routes = new RouteBuilder[2];
        TwitterStoryPublisherRoute twitterRoute = new TwitterStoryPublisherRoute();
        twitterRoute.config = config;
        twitterRoute.interceptSendToEndpoint("twitter-timeline:USER")
                .skipSendToOriginalEndpoint()
                .to("mock:twitter");
        routes[0] = twitterRoute;
        routes[1] = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(BroadcastHistory.UPDATE_ENDPOINT).to("mock:history");
            }
        };
        return routes;
    }

}