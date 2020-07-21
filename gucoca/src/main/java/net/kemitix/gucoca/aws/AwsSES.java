package net.kemitix.gucoca.aws;


public interface AwsSES {

    String send(String from, AwsSesConfig config);

}
