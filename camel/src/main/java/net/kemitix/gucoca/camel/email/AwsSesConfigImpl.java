package net.kemitix.gucoca.camel.email;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.gucoca.common.spi.AwsSesConfig;

import javax.enterprise.inject.Vetoed;

@Setter
@Getter
@Vetoed
public class AwsSesConfigImpl
        implements AwsSesConfig {

    private String awsRegion;
    private String notificationRecipient;
    private String emailSender;

}
