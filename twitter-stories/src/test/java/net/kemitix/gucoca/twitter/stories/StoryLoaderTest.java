package net.kemitix.gucoca.twitter.stories;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoryLoaderTest
        implements WithAssertions {

    @Mock AmazonS3 amazonS3;
    @Mock ListObjectsV2Result objectsV2Result;
    @Mock List<S3ObjectSummary> summaries;
    @Mock S3ObjectSummary summary1;
    @Mock S3ObjectSummary summary2;
    @Mock S3Object s3Object;
    @Mock S3ObjectInputStream inputStream;
    @Mock ObjectMapper objectMapper;

    @InjectMocks StoryLoader storyLoader;

    @Nested
    @DisplayName("load()")
    public class LoadTests {

        String s3BucketName = "bucket-name";
        String s3BucketPrefix = "bucket-prefix";
        String issueFilename;

        @BeforeEach
        public void setUp() {
            // get key
            given(amazonS3.listObjectsV2(any(ListObjectsV2Request.class)))
                    .willReturn(objectsV2Result);
            given(objectsV2Result.getObjectSummaries())
                    .willReturn(summaries);
            given(summaries.stream())
                    .willReturn(Stream.of(summary1, summary2));
            given(summary1.getKey()).willReturn("key/1");
            issueFilename = "2";
            given(summary2.getKey()).willReturn("key/2");

            // get object
            given(amazonS3.getObject(any(GetObjectRequest.class)))
                    .willReturn(s3Object);

            // parse object
            given(s3Object.getObjectContent())
                    .willReturn(inputStream);
        }

        @Test
        @DisplayName("parse okay")
        public void load() throws IOException {
            //given
            Issue issue = new Issue();
            issue.setIssue(13);
            Story story = new Story();
            issue.setStories(List.of(story));
            given(objectMapper.readValue(inputStream, Issue.class))
                    .willReturn(issue);

            //when
            List<Issue> result = storyLoader.load(s3BucketName, s3BucketPrefix, issueFilename);

            //then
            verify(amazonS3, times(1))
                    .getObject(any(GetObjectRequest.class));

            assertThat(result).containsExactly(issue);
            assertThat(result.get(0).getStories())
                    .allSatisfy(s ->
                            assertThat(s.getIssue())
                                    .isEqualTo(13));
        }

        @Test
        @DisplayName("wrap error in RuntimeException")
        public void wrapError() throws IOException {
            //given
            given(objectMapper.readValue(inputStream, Issue.class))
                    .willThrow(IOException.class);
            //then
            assertThatCode(() -> storyLoader.load(s3BucketName, s3BucketPrefix, issueFilename))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("addStoryToCard()")
    public class AddStoryToCardTest {

        Story story = new Story();
        String s3BucketName = "bucket-name";

        @BeforeEach
        public void setUp() {
            story.setIssue(13);
            story.setUrl("alpha/beta");
        }

        @Test
        @DisplayName("add okay")
        public void addOkay() {
            //given
            given(amazonS3.getObject(any(GetObjectRequest.class)))
                    .willReturn(s3Object);
            given(s3Object.getObjectContent())
                    .willReturn(inputStream);

            //when
            Story result = storyLoader.addStoryCard(story, s3BucketName);
            //then
            assertThat(result.getStoryCardInputStream())
                    .isSameAs(inputStream);
        }
        @Test
        @DisplayName("add not found")
        public void addError() {
            //given
            given(amazonS3.getObject(any(GetObjectRequest.class)))
                    .willThrow(AmazonS3Exception.class);

            //when
            Story result = storyLoader.addStoryCard(story, s3BucketName);
            //then
            assertThat(result.getStoryCardInputStream())
                    .isNull();
        }
    }
}