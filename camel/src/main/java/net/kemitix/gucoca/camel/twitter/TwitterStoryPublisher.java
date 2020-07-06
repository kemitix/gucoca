package net.kemitix.gucoca.camel.twitter;

public interface TwitterStoryPublisher {

    String ENDPOINT = "direct:Gucoca.Twitter.Story.Publish";

    String STORY_HEADER = "Gucoca.Twitter.Story";
    String STORYCARD_HEADER = "Gucoca.Twitter.StoryCard";

}
