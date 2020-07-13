package net.kemitix.gucoca.twitter.stories;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Supplier;

public class StoryTest
        implements WithAssertions {

    Supplier<String> stringGenerator = () -> UUID.randomUUID().toString();

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

    @Test
    @DisplayName("has title")
    public void hasTitle() {
        //given
        var story = new Story();
        var title = stringGenerator.get();
        //when
        story.setTitle(title);
        //then
        assertThat(story.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("has author")
    public void hasAuthor() {
        //given
        var story = new Story();
        var author = stringGenerator.get();
        //when
        story.setAuthor(author);
        //then
        assertThat(story.getAuthor()).isEqualTo(author);
    }

    @Test
    @DisplayName("has url")
    public void hasUrl() {
        //given
        var story = new Story();
        var url = stringGenerator.get();
        //when
        story.setUrl(url);
        //then
        assertThat(story.getUrl()).isEqualTo(url);
    }

    @Test
    @DisplayName("has key")
    public void hasKey() {
        //given
        var story = new Story();
        var key = stringGenerator.get();
        //when
        story.setKey(key);
        //then
        assertThat(story.getKey()).isEqualTo(key);
    }

    @Test
    @DisplayName("has published")
    public void hasPublished() {
        //given
        var story = new Story();
        var published = stringGenerator.get();
        //when
        story.setPublished(published);
        //then
        assertThat(story.getPublished()).isEqualTo(published);
    }
}