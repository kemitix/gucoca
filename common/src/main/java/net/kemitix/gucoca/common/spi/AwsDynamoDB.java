package net.kemitix.gucoca.common.spi;

public interface AwsDynamoDB {

    String scan(String tableName);
    String put(String tableName);

}
