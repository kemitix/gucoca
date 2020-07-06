package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.spi.GucocaConfig;

import javax.inject.Inject;
import java.util.Arrays;

class AwsDynamoDBImpl implements AwsDynamoDB {

    @Inject GucocaConfig config;

    @Override
    public String scan() {
        return awsDDBOperation("Scan");
    }

    @Override
    public String put() {
        return awsDDBOperation("PutItem");
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
