package net.kemitix.gucoca.common.spi;

public interface AwsS3Config
        extends AwsConfig {


    String getS3BucketName();

    String getS3BucketPrefix();
}
