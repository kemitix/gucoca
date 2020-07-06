package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.camel.history.BroadcastHistory;
import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject;

public class GucocaRoutes extends RouteBuilder {

    @Inject PostingFrequency postingFrequency;

    @Override
    public void configure() {
        from(postingFrequency.startTimer())
                .routeId("main")
                .process(postingFrequency.shouldIRun())
                .to(BroadcastHistory.LOAD_ENDPOINT);
    }

}
