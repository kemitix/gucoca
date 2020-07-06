package net.kemitix.gucoca.camel.stories;

import net.kemitix.gucoca.camel.twitter.TwitterStoryPublisher;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.List;
import java.util.Random;

class StorySelector {

    static Random random = new Random();

    @SuppressWarnings("unchecked")
    Processor selectAStory() {
        return exchange -> {
            Message in = exchange.getIn();
            List<Story> stories =
                    in.getHeader(
                            Stories.PUBLISHABLE,
                            List.class);
            Story story = stories.get(
                    random.nextInt(stories.size()));
            in.setHeader(TwitterStoryPublisher.STORY, story);
        };
    }

}
