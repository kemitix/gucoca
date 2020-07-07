package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.spi.GucocaConfig;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

class AwsSESImpl implements AwsSES {

    @Inject GucocaConfig config;

    @Override
    public String send(String from) {
        List<String> options = Arrays.asList(
                "amazonSESClient=#amazonSESClient",
                "region=" + config.getAwsRegion()
        );
        return String.format("aws-ses:%s?%s", from,
                String.join("&", options));
    }
}
