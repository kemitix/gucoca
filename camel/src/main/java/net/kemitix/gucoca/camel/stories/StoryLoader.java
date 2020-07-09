package net.kemitix.gucoca.camel.stories;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Issue;
import net.kemitix.gucoca.spi.Story;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
class StoryLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Inject GucocaConfig config;
    @Inject AmazonS3 amazonS3;

    List<Issue> load() {
        return getKeysForConfigFiles()
                .map(fetchConfigFiles())
                .map(parseAsIssue())
                .collect(Collectors.toList());
    }

    Story addStoryCard(Story story) {
        String cardKey = String.format("content/issue/%d/story-card-%s.webp",
                story.getIssue(), story.slug());
        try {
            InputStream objectContent = amazonS3.getObject(
                    new GetObjectRequest(
                            config.getS3BucketName(),
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

    private Stream<S3ObjectSummary> getKeysForConfigFiles() {
        return amazonS3.listObjectsV2(
                new ListObjectsV2Request()
                        .withBucketName(config.getS3BucketName())
                        .withPrefix(config.getS3BucketPrefix())
        ).getObjectSummaries().stream()
                .filter(s -> s.getKey()
                        .endsWith("/" + config.getStoryFilename())
                )
                ;
    }

}
