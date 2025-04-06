package com.example.rest.kafka;

import com.example.rest.dto.OperationKafkaRequest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaMessageProducer {

    private final ReplyingKafkaTemplate<String, Object, Object> kafkaTemplate;

    @Value("${calculator.request.topic}")
    private String requestTopic;

    @Value("${calculator.response.topic}")
    private String responseTopic;

    public KafkaMessageProducer(ReplyingKafkaTemplate<String, Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public BigDecimal sendAndReceive(OperationKafkaRequest request) throws Exception {
        String correlationId = UUID.randomUUID().toString();

        // Set up the record with headers
        ProducerRecord<String, Object> record = new ProducerRecord<>(requestTopic, request);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, responseTopic.getBytes()));
        record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, correlationId.getBytes()));

        // Send and wait for reply
        RequestReplyFuture<String, Object, Object> future = kafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, Object> response = future.get(10, TimeUnit.SECONDS);

        // Cast and return result
        return new BigDecimal(response.value().toString());
    }
}
