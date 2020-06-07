package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AmazonS3ServiceImpl implements AmazonS3Service {

    Supplier<AmazonS3> amazonS3Supplier = AmazonS3ClientBuilder::defaultClient;
    AtomicReference<AmazonS3> amazonS3 = new AtomicReference<>();

    private AmazonS3 getAmazonS3ClientBuilder() {
        if (amazonS3.get() == null) {
            amazonS3.set(amazonS3Supplier.get());
        }
        return amazonS3.get();
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request request) {
        return getAmazonS3ClientBuilder().listObjectsV2(request);
    }

    @Override
    public S3Object getObject(String bucketName, String key) {
        return getAmazonS3ClientBuilder().getObject(bucketName, key);
    }
}
