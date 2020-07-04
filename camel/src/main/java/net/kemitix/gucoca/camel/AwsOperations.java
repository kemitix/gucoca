package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.spi.GucocaConfig;

import javax.inject.Inject;
import java.util.Arrays;

class AwsOperations {

    @Inject GucocaConfig config;

    String awsS3ListObjects() {
        return awsS3Operation("listObjects");
    }

    String awsS3GetObjects() {
        return awsS3Operation("getObject");
    }

    String awsDDBScan() {
        return awsDDBOperation("Scan");
    }

    String awsDDBPut() {
        return awsDDBOperation("PutItem");
    }

    private String awsS3Operation(String operation) {
        String options = String.join("&", Arrays.asList(
                "amazonS3Client=#amazonS3Client",
                "region=" + config.getAwsRegion(),
                "operation=" + operation
        ));
        return String.format("aws-s3://%s?%s",
                config.getS3BucketName(),
                options);
    }

    private String awsDDBOperation(String operation) {
        String options = String.join("&", Arrays.asList(
                "amazonDDBClient=#amazonDDBClient",
                "operation=" + operation
        ));
        return String.format("aws-ddb://%s?%s",
                config.getDDbTableName(),
                options);
    }

}
