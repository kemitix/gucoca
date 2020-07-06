package net.kemitix.gucoca.camel.aws;

public interface AwsS3 {

    String listObjects(String prefix);
    String getObjects();

}
