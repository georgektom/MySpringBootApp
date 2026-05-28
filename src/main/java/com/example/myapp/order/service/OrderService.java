package com.example.myapp.order.service;

import com.example.myapp.order.api.CreateOrderRequest;
import com.example.myapp.order.domain.OrderEntity;
import com.example.myapp.order.domain.OrderStatus;
import com.example.myapp.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import com.example.myapp.config.AppAwsProperties;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final SnsClient snsClient;
    private final SqsTemplate sqsTemplate;
    private final AppAwsProperties awsProperties;

    public OrderService(
            OrderRepository orderRepository,
            ObjectMapper objectMapper,
            S3Client s3Client,
            SnsClient snsClient,
            SqsTemplate sqsTemplate,
            AppAwsProperties awsProperties
    ) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.s3Client = s3Client;
        this.snsClient = snsClient;
        this.sqsTemplate = sqsTemplate;
        this.awsProperties = awsProperties;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setCustomerName(request.customerName());
        order.setProductCode(request.productCode());
        order.setQuantity(request.quantity());
        order.setStatus(OrderStatus.CREATED);
        OrderEntity savedOrder = orderRepository.save(order);

        String payload = toPayload(savedOrder);
        String objectKey = "orders/%s.json".formatted(savedOrder.getId());
        uploadToS3(objectKey, payload);

        savedOrder.setS3ObjectKey(objectKey);
        savedOrder.setStatus(OrderStatus.QUEUED);
        publishEvent(savedOrder, payload);
        queueOrder(savedOrder);

        return orderRepository.save(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void markProcessed(Long id) {
        OrderEntity order = getOrder(id);
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }

    private void uploadToS3(String objectKey, String payload) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(objectKey)
                .contentType("application/json")
                .build();

        s3Client.putObject(request, RequestBody.fromString(payload));
    }

    private void publishEvent(OrderEntity order, String payload) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(awsProperties.getSns().getOrderTopicArn())
                .subject("order-created-%s".formatted(order.getId()))
                .message(payload)
                .build();
        snsClient.publish(request);
    }

    private void queueOrder(OrderEntity order) {
        sqsTemplate.send(to -> to
                .queue(awsProperties.getSqs().getOrderQueue())
                .payload(new OrderProcessingMessage(order.getId(), "ORDER_CREATED")));
    }

    private String toPayload(OrderEntity order) {
        try {
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize order payload", exception);
        }
    }
}
