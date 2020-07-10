package net.kemitix.gucoca.camel.aws;

import net.kemitix.gucoca.common.AwsDdbConfig;
import net.kemitix.gucoca.common.spi.AwsDynamoDB;

import java.util.Arrays;

class AwsDynamoDBImpl implements AwsDynamoDB {

    @Override
    public String scan(AwsDdbConfig config) {
        return awsDDBOperation("Scan", config);
    }

    @Override
    public String put(AwsDdbConfig config) {
        return awsDDBOperation("PutItem", config);
    }

    private String awsDDBOperation(String operation, AwsDdbConfig config) {
        String options = String.join("&", Arrays.asList(
                "amazonDDBClient=#amazonDDBClient",
                "operation=" + operation
        ));
        return String.format("aws-ddb://%s?%s",
                config.getDDbTableName(),
                options);
    }

}
