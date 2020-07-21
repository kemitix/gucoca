package net.kemitix.gucoca.aws;

public interface AwsS3Config
        extends AwsConfig {


    String getS3BucketName();

    String getS3BucketPrefix();
}
