package net.kemitix.gucoca.camel;

import lombok.Getter;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.List;

class StoryAggregator {

    @Getter
    private final long completionTimeoutMillis = 1000L;

    @SuppressWarnings("unchecked")
    Processor putStoriesInHeader() {
        return exchange -> {
            Message in = exchange.getIn();
            List<Story> body = in.getBody(List.class);
            in.setHeader(Headers.STORIES_PUBLISHABLE, body);
            in.setHeader(Headers.STORIES_COUNT, body.size());
            in.setBody(null);
        };
    }

}
