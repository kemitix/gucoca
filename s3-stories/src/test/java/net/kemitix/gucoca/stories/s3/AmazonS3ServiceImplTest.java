package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceImplTest
        implements WithAssertions {

    private final AmazonS3ServiceImpl service = new AmazonS3ServiceImpl();

    private final ListObjectsV2Request listObjectV2Request;
    private final ListObjectsV2Result listObjectV2Result;
    private final S3Object s3Object;
    private final AmazonS3 amazonS3Client;

    public AmazonS3ServiceImplTest(
            @Mock ListObjectsV2Request listObjectV2Request,
            @Mock ListObjectsV2Result listObjectV2Result,
            @Mock S3Object s3Object,
            @Mock AmazonS3 amazonS3Client
    ) {
        this.listObjectV2Request = listObjectV2Request;
        this.listObjectV2Result = listObjectV2Result;
        this.s3Object = s3Object;
        this.amazonS3Client = amazonS3Client;
    }

    @BeforeEach
    public void setUp() {
        service.amazonS3Supplier = () -> amazonS3Client;
    }

    @Test
    @DisplayName("Delegates listObjectV2 call")
    public void listObjectV2Delegates() {
        //given
        given(amazonS3Client.listObjectsV2(listObjectV2Request))
                .willReturn(listObjectV2Result);
        //when
        ListObjectsV2Result result = service.listObjectsV2(listObjectV2Request);
        //then
        assertThat(result).isSameAs(listObjectV2Result);
    }

    @Test
    @DisplayName("Delegates getObject call")
    public void getObjectDelegates() {
        //given
        String bucketName = UUID.randomUUID().toString();
        String key = UUID.randomUUID().toString();
        given(amazonS3Client.getObject(bucketName, key))
                .willReturn(s3Object);
        //when
        S3Object result = service.getObject(bucketName, key);
        //then
        assertThat(result).isSameAs(s3Object);
    }
}