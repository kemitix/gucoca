package net.kemitix.gucoca.camel.stories;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.component.aws.s3.S3Constants;

import javax.inject.Inject;

/**
 * Scans the configured S3 Bucket for files and passes the S3ObjectSummary for
 * each to the s3-stories endpoint.
 */
class AwsS3Stories {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Inject GucocaConfig config;

    private static S3ObjectSummary getS3ObjectSummary(Exchange exchange) {
        return exchange.getIn()
                .getBody(S3ObjectSummary.class);
    }

    Processor setBodyToStory() {
        return exchange -> {
            Message in = exchange.getIn();
            S3Object s3Object = in.getBody(S3Object.class);
            S3ObjectInputStream objectContent =
                    s3Object.getObjectContent();
            Story story = objectMapper.readValue(objectContent, Story.class);
            story.setKey(s3Object.getKey());
            in.setBody(story, Story.class);
        };
    }

    Processor setHeaderForKeyToDownload() {
        return exchange -> {
            Message in = exchange.getIn();
            in.setHeader(S3Constants.KEY,
                    in.getBody(S3ObjectSummary.class)
                            .getKey());
        };
    }

    Predicate toFilename() {
        return exchange ->
                getS3ObjectSummary(exchange)
                        .getKey()
                        .endsWith("/" + config.getStoryFilename());
    }

    Predicate toPrefix() {
        return exchange ->
                getS3ObjectSummary(exchange)
                        .getKey()
                        .startsWith(config.getS3BucketPrefix());
    }

    Processor extractObjectSummaries() {
        return exchange -> {
            Message in = exchange.getIn();
            in.setBody(
                    in.getBody(ObjectListing.class)
                            .getObjectSummaries());
        };
    }

}
