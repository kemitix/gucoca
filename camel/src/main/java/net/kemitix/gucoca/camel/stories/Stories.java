package net.kemitix.gucoca.camel.stories;

public interface Stories {

    String LOAD_STORIES = "direct:Guacoca.Stories.Load";
    String SELECT_STORY = "direct:Guacoca.Stories.Select";

    String PUBLISHED = "Gucoca.Stories.Published";
    String PUBLISHABLE = "Gucoca.Stories.Publishable";
    String PUBLISHABLE_COUNT = "Gucoca.Stories.Publishable.Count";

}
