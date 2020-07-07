package net.kemitix.gucoca.spi;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.inject.Vetoed;

@Setter
@Getter
@Vetoed
public class GucocaConfig {

    private long startFrequencySeconds;
    private int percentChanceToPost;
    private String awsRegion;
    private String s3BucketName;
    private String s3BucketPrefix;
    private String storyFilename;
    private String dDbTableName;
    private boolean twitterEnabled;
    private long twitterDelayMillis;
    private String twitterApiKey;
    private String twitterApiSecretKey;
    private String twitterAccessToken;
    private String twitterAccessTokenSecret;
    private int noRepeatDays;
    private String notificationRecipient;
    private String emailSender;
}
