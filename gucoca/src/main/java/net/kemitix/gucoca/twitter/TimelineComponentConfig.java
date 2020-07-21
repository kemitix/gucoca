package net.kemitix.gucoca.twitter;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.component.twitter.timeline.TwitterTimelineComponent;

class TimelineComponentConfig {

    @PropertyInject("gucoca.twitterstories.token.access")
    String twitterAccessToken;

    @PropertyInject("gucoca.twitterstories.token.secret")
    String twitterAccessTokenSecret;

    @PropertyInject("gucoca.twitterstories.api.key")
    String twitterApiKey;

    @PropertyInject("gucoca.twitterstories.api.secret")
    String twitterApiSecretKey;

    void prepare(CamelContext camelContext) {
        TwitterTimelineComponent component =
                camelContext.getComponent("twitter-timeline",
                        TwitterTimelineComponent.class);
        component.setAccessToken(twitterAccessToken);
        component.setAccessTokenSecret(twitterAccessTokenSecret);
        component.setConsumerKey(twitterApiKey);
        component.setConsumerSecret(twitterApiSecretKey);
    }

}
