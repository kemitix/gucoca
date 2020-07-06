package net.kemitix.gucoca.camel;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import net.kemitix.gucoca.camel.twitter.TwitterStoryPublisher;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.aws.s3.S3Constants;

import java.nio.file.Paths;

class StoryCardProcessors {

    /**
     * Puts the story card's S3 KEY, from the {@link Story} in the
     * {@link TwitterStoryPublisher#STORY_HEADER} header into the {@link S3Constants#KEY}
     * header to allow it be be downloaded.
     */
    Processor setS3GetObjectKeyHeader() {
        return exchange -> {
            Message in = exchange.getIn();
            Story story = in.getHeader(TwitterStoryPublisher.STORY_HEADER, Story.class);
            String storyKey = story.getKey();
            String cardKey = Paths.get(storyKey).getParent()
                    .resolve("gucoca.webp").toString();
            in.setHeader(S3Constants.KEY, cardKey);
        };
    }

    /**
     * Puts the {@link S3ObjectInputStream} of the {@link S3Object} in the BODY
     * in the {@link TwitterStoryPublisher#STORYCARD_HEADER} header.
     */
    Processor setS3ObjectInputStreamHeader() {
        return exchange -> {
            Message in = exchange.getIn();
            S3ObjectInputStream objectContent =
                    in.getBody(S3Object.class)
                            .getObjectContent();
            in.setHeader(TwitterStoryPublisher.STORYCARD_HEADER, objectContent);
        };
    }

}
