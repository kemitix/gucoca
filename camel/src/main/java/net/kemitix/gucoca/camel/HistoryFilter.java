package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Predicate;

import java.util.List;

class HistoryFilter {

    @SuppressWarnings("unchecked")
    Predicate dropStoriesInHistory() {
        return exchange -> {
            Message in = exchange.getIn();
            List<String> listHeader = in.getHeader(Headers.BROADCAST_LIST, List.class);
            Story story = in.getBody(Story.class);
            String slug = story.slug();
            return !listHeader.contains(slug);
        };
    }
}
