package net.kemitix.gucoca.aws;

public interface AwsDynamoDB {

    String scan(String tableName);
    String put(String tableName);

}
