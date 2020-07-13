package net.kemitix.gucoca.twitter.stories;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.StatusUpdate;

import java.io.InputStream;
import java.util.Collections;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class TwitterStoryPostTest
        implements WithAssertions {

    @Mock
    Random random;

    @InjectMocks
    TwitterStoryPost twitterStoryPost;

    @Mock
    InputStream inputStream;

    @Test
    @DisplayName("Create a StatusUpdate")
    public void createStatusUpdate() {
        //given
        Story story = new Story();
        story.setTitle("title");
        story.setAuthor("author");
        story.setUrl("URL");
        story.setBlurb(Collections.singletonList("blurb"));
        story.setStoryCardInputStream(inputStream);

        //when
        StatusUpdate statusUpdate = twitterStoryPost.preparePost(story);

        //then
        assertThat(statusUpdate.getStatus()).isEqualTo(
                "Read title by author. blurb URL #free #fiction #shortstory"
        );
    }

    @Test
    @DisplayName("Create StatusUpdate and drops tags when no space")
    public void statusUpdateDropsTagsWhenNoSpace() {
        //given
        Story story = new Story();
        story.setTitle("title title title title title title title title title title title");
        story.setAuthor("author author author author author author author author author");
        story.setUrl("URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL URL");
        story.setBlurb(Collections.singletonList("blurb blurb blurb blurb blurb blurb blurb blurb"));
        story.setStoryCardInputStream(inputStream);

        //when
        StatusUpdate statusUpdate = twitterStoryPost.preparePost(story);

        //then
        assertThat(statusUpdate.getStatus().length()).isLessThanOrEqualTo(280);
        assertThat(statusUpdate.getStatus()).endsWith(" URL #free #fiction");
    }

}