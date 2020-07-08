package net.kemitix.gucoca.camel.stories;

public interface Stories {

    String LOAD_STORIES = "direct:Gucoca.Stories.Load";
    String ADD_STORY_CARD = "direct:Gucoca.Stories.Select";
    String NOTIFY_SELECTION = "direct:Gucoca.Stories.Notify";

    String PUBLISHED = "Gucoca.Stories.Published";
    String PUBLISHABLE = "Gucoca.Stories.Publishable";
    String PUBLISHABLE_COUNT = "Gucoca.Stories.Publishable.Count";

}
