package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.common.spi.AwsS3;
import net.kemitix.gucoca.common.spi.AwsS3Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class AwsS3Impl implements AwsS3 {

    @Override
    public String listObjects(String prefix, AwsS3Config config) {
        return awsS3Operation("listObjects",
                Collections.singletonList("prefix=" + prefix),
                config);
    }

    @Override
    public String getObjects(AwsS3Config config) {
        return awsS3Operation("getObject",
                Collections.emptyList(),
                config);
    }

    private String awsS3Operation(
            String operation,
            List<String> additionalOptions,
            AwsS3Config config
    ) {
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
