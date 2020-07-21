package net.kemitix.gucoca.twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.StatusUpdate;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TwitterStoryPublisherRouteTest
        implements WithAssertions {

    private final TimelineComponentConfig timelineComponentConfig;
    private final TwitterStoryPost twitterStoryPost;
    private final String s3BucketName = "s3-bucket-name";
    private boolean twitterEnabled;

    Story story = new Story();
    String storyJson;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        story.setTitle("title");
        story.setAuthor("author");
        storyJson = new ObjectMapper().writeValueAsString(story);
    }

    TwitterStoryPublisherRouteTest(
            @Mock TimelineComponentConfig timelineComponentConfig,
            @Mock TwitterStoryPost twitterStoryPost
    ) {
        this.timelineComponentConfig = timelineComponentConfig;
        this.twitterStoryPost = twitterStoryPost;
    }

    @Nested
    @DisplayName("UnmarshallStory")
    class UnmarshallStoryRouteTests extends CamelTestSupport {
        @Test
        @DisplayName("Unmarshall a story")
        public void unmarshallAStory() {
            //given
            MockEndpoint mock = getMockEndpoint("mock:end");
            mock.expectedMessageCount(1);

            //when
            template.sendBody("direct:start", storyJson);

            //then
            List<Exchange> receivedExchanges = mock.getReceivedExchanges();
            assertThat(receivedExchanges).hasSize(1);
            Story body = receivedExchanges.get(0).getIn().getBody(Story.class);
            assertThat(body).isEqualTo(story);
        }

        @Override
        protected RoutesBuilder[] createRouteBuilders() {
            return wrapRoute("UnmarshalStory");
        }
    }

    @Nested
    @DisplayName("isEnabled")
    class IsEnabledRouteTests {
        @Nested
        @DisplayName("When is Enabled")
        class WhenIsEnabledTest extends CamelTestSupport {
            @Test
            @DisplayName("When enabled then pass message on")
            public void whenEnabledThenPass() throws InterruptedException {
                //given
                MockEndpoint mockUpdate = getMockEndpoint("mock:update");
                mockUpdate.expectedMessageCount(1);
                MockEndpoint mockPublish = getMockEndpoint("mock:publish");
                mockPublish.expectedMessageCount(1);

                //when
                template.sendBody("direct:start", story);

                //then
                assertMockEndpointsSatisfied();

                List<Exchange> receivedUpdates = mockUpdate.getReceivedExchanges();
                assertThat(receivedUpdates).hasSize(1);
                Story bodyUpdate = receivedUpdates.get(0).getIn().getBody(Story.class);
                assertThat(bodyUpdate).isEqualTo(story);

                List<Exchange> receivedPublish = mockPublish.getReceivedExchanges();
                assertThat(receivedPublish).hasSize(1);
                Story bodyPublish = receivedUpdates.get(0).getIn().getBody(Story.class);
                assertThat(bodyPublish).isEqualTo(story);
            }

            @Override
            protected RoutesBuilder[] createRouteBuilders() {
                twitterEnabled = true;
                return wrapRoute("isEnabled");
            }
        }

        @Nested
        @DisplayName("When is Disabled")
        class WhenIsDisabledTest extends CamelTestSupport {
            @Test
            @DisplayName("When disable then do not pass message on")
            public void whenDisabledThenDoNotPass() throws InterruptedException {
                //given
                MockEndpoint mockUpdate = getMockEndpoint("mock:update");
                mockUpdate.expectedMessageCount(0);
                MockEndpoint mockPublish = getMockEndpoint("mock:publish");
                mockPublish.expectedMessageCount(0);

                //when
                template.sendBody("direct:start", story);

                //then
                assertMockEndpointsSatisfied();

                List<Exchange> receivedExchanges = mockUpdate.getReceivedExchanges();
                assertThat(receivedExchanges).hasSize(0);
            }

            @Override
            protected RoutesBuilder[] createRouteBuilders() {
                twitterEnabled = false;
                return wrapRoute("isEnabled");
            }
        }
    }

    @Nested
    @DisplayName("Post")
    class PostRouteTests extends CamelTestSupport {

        StatusUpdate statusUpdate = new StatusUpdate("test");

        @Test
        @DisplayName("post")
        public void post() throws InterruptedException {
            //given
            MockEndpoint mockTwitter = getMockEndpoint("mock:twitter");
            mockTwitter.expectedMessageCount(1);
            MockEndpoint mockEnd = getMockEndpoint("mock:end");
            given(twitterStoryPost.preparePost(story, s3BucketName))
                    .willReturn(statusUpdate);

            //when
            template.sendBody("direct:start", story);

            //then
            assertMockEndpointsSatisfied();

            List<Exchange> twitterExchanges = mockTwitter.getReceivedExchanges();
            assertThat(twitterExchanges).hasSize(1);
            assertThat(twitterExchanges.get(0).getIn()
                    .getBody(StatusUpdate.class))
                    .as("status update sent to twitter")
                    .isSameAs(statusUpdate);

            List<Exchange> endExchange = mockEnd.getReceivedExchanges();
            assertThat(endExchange).hasSize(1);
            assertThat(endExchange.get(0).getIn()
                    .getBody(Story.class))
                    .as("Story in body")
                    .isEqualTo(story);
        }

        @Override
        protected RoutesBuilder[] createRouteBuilders() {
            return wrapRoute("Post");
        }
    }

    private RoutesBuilder[] wrapRoute(final String tag) {
        RoutesBuilder subjects = routesBuilder();
        RoutesBuilder monitor = new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .to("direct:Gucoca.TwitterStories." + tag)
                        .to("mock:end");
            }
        };
        return new RoutesBuilder[]{subjects, monitor};
    }

    RoutesBuilder routesBuilder() {
        var route = new TwitterStoryPublisherRoute();
        route.twitterEnabled = twitterEnabled;
        route.timelineComponentConfig = timelineComponentConfig;
        route.twitterStoryPost = twitterStoryPost;
        route.s3BucketName = s3BucketName;
        route.interceptSendToEndpoint("twitter-timeline:USER")
                .skipSendToOriginalEndpoint()
                .to("mock:twitter");
        route.interceptSendToEndpoint("direct:Gucoca.TwitterStoryPublish.Publish.Enabled")
                .skipSendToOriginalEndpoint()
                .to("mock:publish");
        route.interceptSendToEndpoint(BroadcastHistory.UPDATE_ENDPOINT)
                .skipSendToOriginalEndpoint()
                .to("mock:update");
        return route;
    }

}