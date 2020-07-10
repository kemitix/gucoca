package net.kemitix.gucoca.common.spi;

public interface AwsS3 {

    String listObjects(String prefix, AwsS3Config config);
    String getObjects(AwsS3Config config);

}
