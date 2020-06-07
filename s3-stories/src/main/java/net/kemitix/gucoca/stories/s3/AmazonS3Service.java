package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;

public interface AmazonS3Service {
    ListObjectsV2Result listObjectsV2(ListObjectsV2Request request);

    S3Object getObject(String bucketName, String key);
}
