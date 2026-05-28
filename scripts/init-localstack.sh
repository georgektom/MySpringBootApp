#!/bin/bash
set -euo pipefail

awslocal s3 mb s3://myapp-local-bucket || true
awslocal sqs create-queue --queue-name myapp-order-queue || true
awslocal sns create-topic --name myapp-order-events || true
awslocal sqs set-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/myapp-order-queue \
  --attributes '{
    "Policy":"{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"Allow-SNS-SendMessage\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"sns.amazonaws.com\"},\"Action\":\"sqs:SendMessage\",\"Resource\":\"arn:aws:sqs:us-east-1:000000000000:myapp-order-queue\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"arn:aws:sns:us-east-1:000000000000:myapp-order-events\"}}}]}"
  }' || true
awslocal sns subscribe \
  --topic-arn arn:aws:sns:us-east-1:000000000000:myapp-order-events \
  --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-east-1:000000000000:myapp-order-queue || true

awslocal secretsmanager create-secret \
  --name /myapp/common \
  --secret-string '{
    "spring.datasource.username":"myapp",
    "spring.datasource.password":"myapp",
    "app.aws.s3.bucket-name":"myapp-local-bucket",
    "app.aws.sns.order-topic-arn":"arn:aws:sns:us-east-1:000000000000:myapp-order-events",
    "app.aws.sqs.order-queue":"myapp-order-queue"
  }' || true

awslocal secretsmanager create-secret \
  --name /myapp/local \
  --secret-string '{
    "spring.datasource.url":"jdbc:mysql://localhost:3306/myapp?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true",
    "app.environment":"local"
  }' || true
