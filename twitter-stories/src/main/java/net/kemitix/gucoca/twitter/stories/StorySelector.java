package net.kemitix.gucoca.twitter.stories;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StorySelector {

    static Random random = new Random();

    public Story select(StoryContext storyContext) {
        List<Story> stories =
                storyContext
                        .stories()
                        .stream()
                        .filter(story -> story.isPublished(Instant.now()))
                        .filter(story -> !story.getBlurb().isEmpty())
                        .filter(story ->
                                !storyContext
                                        .getHistory()
                                        .contains(story.slug()))
                        .collect(Collectors.toList());
        return stories.get(
                random.nextInt(stories.size()));
    }

}
