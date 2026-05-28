package com.example.myapp.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.aws")
public class AppAwsProperties {

    private final S3 s3 = new S3();
    private final Sns sns = new Sns();
    private final Sqs sqs = new Sqs();

    public S3 getS3() {
        return s3;
    }

    public Sns getSns() {
        return sns;
    }

    public Sqs getSqs() {
        return sqs;
    }

    public static class S3 {
        @NotBlank
        private String bucketName;

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Sns {
        @NotBlank
        private String orderTopicArn;

        public String getOrderTopicArn() {
            return orderTopicArn;
        }

        public void setOrderTopicArn(String orderTopicArn) {
            this.orderTopicArn = orderTopicArn;
        }
    }

    public static class Sqs {
        @NotBlank
        private String orderQueue;

        public String getOrderQueue() {
            return orderQueue;
        }

        public void setOrderQueue(String orderQueue) {
            this.orderQueue = orderQueue;
        }
    }
}

