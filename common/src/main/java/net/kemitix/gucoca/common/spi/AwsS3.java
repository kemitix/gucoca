package net.kemitix.gucoca.common.spi;

public interface AwsS3 {

    String listObjects(String prefix);
    String getObjects();

}
