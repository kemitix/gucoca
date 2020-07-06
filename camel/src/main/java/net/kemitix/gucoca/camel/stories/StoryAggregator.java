package net.kemitix.gucoca.camel.stories;

import lombok.Getter;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.List;

class StoryAggregator {

    @SuppressWarnings("unchecked")
    Processor putStoriesInHeader() {
        return exchange -> {
            Message in = exchange.getIn();
            List<Story> body = in.getBody(List.class);
            in.setHeader(Stories.PUBLISHABLE, body);
            in.setHeader(Stories.PUBLISHABLE_COUNT, body.size());
            in.setBody(null);
        };
    }

}
