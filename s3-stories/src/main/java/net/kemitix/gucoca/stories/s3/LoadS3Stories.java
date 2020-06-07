package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.java.Log;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.utils.ServiceSupplier;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Iterator;

@Log
public class LoadS3Stories extends AbstractItemReader {

    @Inject JobContext jobContext;

    ServiceSupplier serviceSupplier = ServiceSupplier.create();

    private Iterator<S3ObjectSummary> objectSummaries;
    private String nextPageToken;
    private AmazonS3Service amazonS3;

    @Override
    public void open(Serializable checkpoint) {
        amazonS3 = serviceSupplier.findOne(AmazonS3Service.class);
        requestPage(null);
    }

    @Override
    public Object readItem() {
        if (objectSummaries.hasNext()) {
            return objectSummaries.next();
        }
        if (nextPageToken != null) {
            requestPage(nextPageToken);
            return readItem();
        }
        return null;
    }

    private void requestPage(String pageToken) {
        log.info(String.format("requestPage(%s)", pageToken));
        ListObjectsV2Result result = amazonS3.listObjectsV2(request(pageToken));
        objectSummaries = result.getObjectSummaries().iterator();
        nextPageToken = result.getNextContinuationToken();
        log.info(String.format(
                "Fetched %d items", result.getObjectSummaries().size()));
    }

    private ListObjectsV2Request request(String pageToken) {
        GucocaConfig config = ((JobStateData) jobContext.getTransientUserData())
                .getConfig();
        String bucketName = config.getS3BucketName();
        String bucketPrefix = config.getS3BucketPrefix();
        return new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(bucketPrefix)
                .withContinuationToken(pageToken);
    }
}
