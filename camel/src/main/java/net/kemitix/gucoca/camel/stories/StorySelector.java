package net.kemitix.gucoca.camel.stories;

import net.kemitix.gucoca.spi.Story;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StorySelector {

    static Random random = new Random();

    public Story select(StoryContext storyContext) {
        List<Story> stories =
                storyContext
                        .getStories()
                        .stream()
                        .filter(story ->
                                !storyContext
                                        .getHistory()
                                        .contains(story.slug()))
                        .collect(Collectors.toList());
        return stories.get(
                random.nextInt(stories.size()));
    }

}
