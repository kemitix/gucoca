package net.kemitix.gucoca.twitter.stories;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class StoryLoader {

    @Inject ObjectMapper mapper;
    @Inject AmazonS3 amazonS3;

    public List<Issue> load(
            String s3BucketName,
            String s3BucketPrefix,
            String issueFilename
    ) {
        return getKeysForConfigFiles(s3BucketName, s3BucketPrefix, issueFilename)
                .map(fetchConfigFiles())
                .map(parseAsIssue())
                .collect(Collectors.toList());
    }

    public Story addStoryCard(Story story, String s3BucketName) {
        String cardKey = String.format("content/issue/%d/story-card-%s.webp",
                story.getIssue(), story.slug());
        try {
            InputStream objectContent = amazonS3.getObject(
                    new GetObjectRequest(
                            s3BucketName,
                            cardKey))
                    .getObjectContent();
            story.setStoryCardInputStream(objectContent);
            return story;
        } catch (AmazonS3Exception e) {
            log.warn("Key not found: {}", cardKey);
            return story;
        }
    }

    private Function<S3Object, Issue> parseAsIssue() {
        return s3Object -> {
            InputStream content = s3Object.getObjectContent();
            try {
                Issue issue = mapper.readValue(content, Issue.class);
                issue.getStories()
                        .forEach(story -> story.setIssue(issue.getIssue()));
                return issue;
            } catch (IOException e) {
                throw new RuntimeException("Error parsing as story", e);
            }
        };
    }

    private Function<S3ObjectSummary, S3Object> fetchConfigFiles() {
        return s -> amazonS3.getObject(
                new GetObjectRequest(s.getBucketName(), s.getKey()));
    }

    private Stream<S3ObjectSummary> getKeysForConfigFiles(
            String s3BucketName,
            String s3BucketPrefix,
            String issueFilename
    ) {
        return amazonS3.listObjectsV2(
                new ListObjectsV2Request()
                        .withBucketName(s3BucketName)
                        .withPrefix(s3BucketPrefix)
        ).getObjectSummaries().stream()
                .filter(s -> s.getKey()
                        .endsWith("/" + issueFilename)
                )
                ;
    }

}
