package com.java110.core.utils;

import com.java110.core.cache.MappingCache;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class S3Util {

    public static final String DOMAIN = "OSS";
    public static final String S3_SWITCH = "OSS_SWITCH";
    public static final String S3_SWITCH_S3 = "S3";
    public static final String S3_SWITCH_FTP = "FTP";
    public static final String ENDPOINT = "endpoint";
    public static final String ACCESS_KEY_ID = "accessKeyId";
    public static final String ACCESS_KEY_SECRET = "accessKeySecret";
    public static final String BUCKET_NAME = "bucketName";
    public static final String REGION = "region";
    public static final String PATH_STYLE = "pathStyle";

    public static S3Client getS3Client() {
        String endpoint = MappingCache.getValue(DOMAIN, ENDPOINT);
        String accessKeyId = MappingCache.getValue(DOMAIN, ACCESS_KEY_ID);
        String accessKeySecret = MappingCache.getValue(DOMAIN, ACCESS_KEY_SECRET);
        String region = MappingCache.getValue(DOMAIN, REGION);
        if (region == null || region.isEmpty()) {
            region = "us-east-1";
        }
        String pathStyle = MappingCache.getValue(DOMAIN, PATH_STYLE);
        boolean forcePathStyle = "true".equals(pathStyle);
        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .forcePathStyle(forcePathStyle)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, accessKeySecret)
                ))
                .build();
        return s3Client;
    }

    public static void uploadByInputStream(S3Client s3Client, InputStream inputStream,
                                           String objectName) {
        String bucketName = MappingCache.getValue(DOMAIN, BUCKET_NAME);
        uploadByInputStream(s3Client, inputStream, bucketName, objectName);
    }

    public static void uploadByInputStream(S3Client s3Client, InputStream inputStream, String bucketName,
                                           String objectName) {
        try {
            byte[] bytes = toByteArray(inputStream);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s3Client != null) {
                s3Client.close();
            }
        }
    }

    public static void uploadByFile(S3Client s3Client, File file, String bucketName, String objectName) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();
            s3Client.putObject(request, RequestBody.fromFile(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s3Client != null) {
                s3Client.close();
            }
        }
    }

    public static void deleteFile(S3Client s3Client, String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    public static InputStream getInputStreamByS3(S3Client s3Client, String key) {
        String bucketName = MappingCache.getValue(DOMAIN, BUCKET_NAME);
        return getInputStreamByS3(s3Client, bucketName, key);
    }

    public static InputStream getInputStreamByS3(S3Client s3Client, String bucketName, String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> queryAllObject(S3Client s3Client, String bucketName) {
        List<String> results = new ArrayList<String>();
        try {
            ListObjectsRequest request = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsResponse response = s3Client.listObjects(request);
            for (S3Object object : response.contents()) {
                results.add(" - " + object.key() + "  " + "(size = " + object.size() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s3Client != null) {
                s3Client.close();
            }
        }
        return results;
    }

    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
