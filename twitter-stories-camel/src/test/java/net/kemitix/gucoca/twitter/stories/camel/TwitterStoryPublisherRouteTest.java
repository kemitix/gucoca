package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.Story;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPost;
import net.kemitix.gucoca.twitter.stories.TwitterStoryPublisher;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.StatusUpdate;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.kemitix.gucoca.twitter.stories.TwitterStoryPublisher.STORY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TwitterStoryPublisherRouteTest
        implements WithAssertions {

    private final TimelineComponentConfig timelineComponentConfig;
    private final TwitterStoryPost twitterStoryPost;

    TwitterStoryPublisherRouteTest(
            @Mock TimelineComponentConfig timelineComponentConfig,
            @Mock TwitterStoryPost twitterStoryPost
    ) {
        this.timelineComponentConfig = timelineComponentConfig;
        this.twitterStoryPost = twitterStoryPost;
    }

    @Nested
    @DisplayName("When Twitter is Enabled")
    class Enabled extends CamelTestSupport {
        @Test
        @DisplayName("Tweets a Story")
        public void tweetsStory() throws InterruptedException {
            //given
            MockEndpoint mock = getMockEndpoint("mock:twitter");
            mock.expectedMessageCount(1);

            Story story = new Story();

            //when
            template.sendBodyAndHeader(TwitterStoryPublisher.PUBLISH, null, STORY, story);

            //then
            assertMockEndpointsSatisfied();

            List<Exchange> receivedExchanges = mock.getReceivedExchanges();
            assertThat(receivedExchanges).hasSize(1);

            verify(twitterStoryPost).preparePost(any(Story.class));
        }

        @Override
        protected RoutesBuilder[] createRouteBuilders() {
            return routeBuilders(true);
        }
    }

    @Nested
    @DisplayName("Whe Twitter is Disabled")
    class Disabled extends CamelTestSupport {

        @Test
        @DisplayName("When disable don't tweet story")
        public void whenDisabledDoNoTweetStory() throws InterruptedException {
            //given
            MockEndpoint mock = getMockEndpoint("mock:twitter");
            mock.expectedMessageCount(0);

            //when
            template.sendBody(TwitterStoryPublisher.PUBLISH, null);

            //then
            assertMockEndpointsSatisfied(2, TimeUnit.SECONDS);

            verify(twitterStoryPost, never()).preparePost(any(Story.class));
        }

        @Override
        protected RoutesBuilder[] createRouteBuilders() {
            return routeBuilders(false);
        }
    }

    RoutesBuilder[] routeBuilders(boolean twitterEnabled) {
        RouteBuilder[] routes = new RouteBuilder[2];
        TwitterStoryPublisherRoute twitterRoute = new TwitterStoryPublisherRoute();
        twitterRoute.twitterEnabled = twitterEnabled;
        twitterRoute.timelineComponentConfig = timelineComponentConfig;
        twitterRoute.twitterStoryPost = twitterStoryPost;
        twitterRoute.interceptSendToEndpoint("twitter-timeline:USER")
                .skipSendToOriginalEndpoint()
                .to("mock:twitter");
        routes[0] = twitterRoute;
        routes[1] = new RouteBuilder() {
            @Override
            public void configure() {
                from(BroadcastHistory.UPDATE_ENDPOINT).to("mock:history");
            }
        };
        return routes;
    }

}