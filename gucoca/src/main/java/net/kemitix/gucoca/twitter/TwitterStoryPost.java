package net.kemitix.gucoca.twitter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import twitter4j.StatusUpdate;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TwitterStoryPost {

    private static final int MAX_TWEET_LENGTH = 280;

    @Inject
    Random random;

    @Inject
    AmazonS3 amazonS3;

    public StatusUpdate preparePost(Story story, String s3BucketName) {
        String title = story.getTitle();
        String author = story.getAuthor();
        List<String> blurbs = story.getBlurb();
        String blurb = blurbs.get(random.nextInt(blurbs.size()));
        String url = story.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Read %s by %s. %s %s",
                title, author, blurb, url));
        List<String> hashtags = Arrays.asList("free", "fiction", "shortstory");
        hashtags.forEach(hashtag -> {
            if (sb.length() + 2 + hashtag.length() <= MAX_TWEET_LENGTH) {
                sb.append(String.format(" #%s", hashtag));
            }
        });
        InputStream cardStream = getStoryCardInputStream(story, s3BucketName);
        // can't use attachmentUrl as that is for links on twitter itself
        return new StatusUpdate(sb.toString())
                .media("story-card", cardStream);
    }

    private InputStream getStoryCardInputStream(Story story, String s3BucketName) {
        try {
            GetObjectRequest request =
                    new GetObjectRequest(
                            s3BucketName,
                            story.cardKey());
            return amazonS3.getObject(request)
                    .getObjectContent();
        } catch (AmazonS3Exception e) {
            throw new RuntimeException(String.format(
                    "Key not found: %s", story.cardKey()), e);
        }
    }

}
