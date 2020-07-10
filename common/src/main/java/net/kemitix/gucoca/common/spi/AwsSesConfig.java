package net.kemitix.gucoca.common.spi;

public interface AwsSesConfig {
    String getAwsRegion();

    String getNotificationRecipient();

    String getEmailSender();
}
