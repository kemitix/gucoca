package net.kemitix.gucoca.twitter.stories.camel;

import org.apache.camel.builder.RouteBuilder;

public class TickTockRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:tick?period=12340")
                .to("log:tock");
    }
}
