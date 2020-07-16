package net.kemitix.gucoca.twitter.stories;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StorySelector {

    @Inject
    Random random;

    public List<Story> stories(StoryContext storyContext) {
        return storyContext
                .stories()
                .stream()
                .filter(story -> story.isPublished(Instant.now()))
                .filter(story -> !story.getBlurb().isEmpty())
                .filter(story ->
                        !storyContext
                                .getHistory()
                                .contains(story.slug()))
                .collect(Collectors.toList());
    }

    public Story select(List<Story> stories) {
        return stories.get(
                random.nextInt(stories.size()));
    }

}
