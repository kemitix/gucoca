package net.kemitix.gucoca.aws;

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
