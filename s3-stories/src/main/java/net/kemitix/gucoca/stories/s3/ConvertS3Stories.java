package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.java.Log;
import net.kemitix.gucoca.spi.JsonObjectParser;
import net.kemitix.gucoca.spi.Story;
import net.kemitix.gucoca.utils.ServiceSupplier;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;

@Log
public class ConvertS3Stories implements ItemProcessor {

    ServiceSupplier serviceSupplier = ServiceSupplier.create();

    @Inject @BatchProperty(name = "filename") String filename;

    @Override
    public Object processItem(Object item) {
        AmazonS3Service amazonS3 = serviceSupplier.findOne(AmazonS3Service.class);
        JsonObjectParser jsonObjectParser =
                serviceSupplier.findOne(JsonObjectParser.class);
        S3ObjectSummary summary = (S3ObjectSummary) item;
        String key = summary.getKey();
        if (key.endsWith("/" + filename)) {
            log.info("Found: " + key);
            String bucketName = summary.getBucketName();
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            S3ObjectInputStream objectContent = s3Object.getObjectContent();
            return jsonObjectParser.fromJson(objectContent, Story.class);
        }
        return null;
    }

}
