package net.kemitix.gucoca.camel.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
class AmazonClientProducer {

    @Produces
    @Named("amazonS3Client")
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.defaultClient();
    }

    @Produces
    @Named("amazonDDBClient")
    public AmazonDynamoDB amazonDynamoDb() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }
}
