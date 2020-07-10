package net.kemitix.gucoca.twitter.stories;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StoryTest
        implements WithAssertions {
    @Test
    @DisplayName("create slug from URL")
    public void slugFromUrl() {
        //given
        String url = "https://www.cossmass.com/stories/the-paper-doll-golems/";
        String expected = "the-paper-doll-golems";
        Story story = new Story();
        story.setUrl(url);
        //when
        String slug = story.slug();
        //then
        assertThat(slug).isEqualTo(expected);
    }
}