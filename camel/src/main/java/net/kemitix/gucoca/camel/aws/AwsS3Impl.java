package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.spi.GucocaConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class AwsS3Impl implements AwsS3 {

    @Inject GucocaConfig config;

    @Override
    public String listObjects(String prefix) {
        return awsS3Operation("listObjects",
                Collections.singletonList("prefix=" + prefix));
    }

    @Override
    public String getObjects() {
        return awsS3Operation("getObject",
                Collections.emptyList());
    }

    private String awsS3Operation(String operation, List<String> additionalOptions) {
        List<String> options = new ArrayList<>();
        options.addAll(Arrays.asList(
                "amazonS3Client=#amazonS3Client",
                "region=" + config.getAwsRegion(),
                "operation=" + operation
        ));
        options.addAll(additionalOptions);
        return String.format("aws-s3://%s?%s",
                config.getS3BucketName(),
                String.join("&", options));
    }

}
