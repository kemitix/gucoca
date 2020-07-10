package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.common.spi.AwsConfig;
import net.kemitix.gucoca.common.spi.AwsSES;
import net.kemitix.gucoca.common.spi.AwsSesConfig;

import java.util.Arrays;
import java.util.List;

class AwsSESImpl implements AwsSES {

    @Override
    public String send(String from, AwsSesConfig config) {
        List<String> options = Arrays.asList(
                "amazonSESClient=#amazonSESClient",
                "region=" + config.getAwsRegion()
        );
        return String.format("aws-ses:%s?%s", from,
                String.join("&", options));
    }
}
