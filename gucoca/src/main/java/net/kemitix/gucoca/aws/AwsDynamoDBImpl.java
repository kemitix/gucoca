package net.kemitix.gucoca.aws;

import net.kemitix.gucoca.common.spi.AwsDynamoDB;

import java.util.Arrays;

class AwsDynamoDBImpl implements AwsDynamoDB {

    @Override
    public String scan(String tableName) {
        return awsDDBOperation("Scan", tableName);
    }

    @Override
    public String put(String tableName) {
        return awsDDBOperation("PutItem", tableName);
    }

    private String awsDDBOperation(String operation, String tableName) {
        String options = String.join("&", Arrays.asList(
                "amazonDDBClient=#amazonDDBClient",
                "operation=" + operation
        ));
        return String.format("aws-ddb://%s?%s",
                tableName,
                options);
    }

}
