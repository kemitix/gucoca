package net.kemitix.gucoca.twitter.stories;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.StatusUpdate;

import java.util.Collections;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TwitterStoryPostTest
        implements WithAssertions {

    @Mock
    Random random;
    @Mock
    AmazonS3 amazonS3;

    @InjectMocks
    TwitterStoryPost twitterStoryPost;

    @Mock
    S3ObjectInputStream inputStream;

    String s3BucketName = "S3-bucket";

    @Mock
    S3Object s3Object;

    @BeforeEach
    public void setUp() {
        given(amazonS3.getObject(any(GetObjectRequest.class)))
                .willReturn(s3Object);
        given(s3Object.getObjectContent())
                .willReturn(inputStream);
    }

    @Test
    @DisplayName("Create a StatusUpdate")
    public void createStatusUpdate() {
        //given
        Story story = new Story();
        story.setTitle("title");
        story.setAuthor("author");
        story.setUrl("URL");
        story.setBlurb(Collections.singletonList("blurb"));

        //when
        StatusUpdate statusUpdate = twitterStoryPost.preparePost(story, s3BucketName);

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

        //when
        StatusUpdate statusUpdate = twitterStoryPost.preparePost(story, s3BucketName);

        //then
        assertThat(statusUpdate.getStatus().length()).isLessThanOrEqualTo(280);
        assertThat(statusUpdate.getStatus()).endsWith(" URL #free #fiction");
    }

}