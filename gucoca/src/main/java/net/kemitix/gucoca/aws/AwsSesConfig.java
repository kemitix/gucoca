package net.kemitix.gucoca.aws;

public interface AwsSesConfig {
    String getAwsRegion();

    String getNotificationRecipient();

    String getEmailSender();
}
