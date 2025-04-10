package com.example.calculator.kafka;

import com.example.calculator.dto.OperationKafkaRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Service
public class KafkaMessageConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Value("${calculator.response.topic}")
    private String responseTopic;

    public KafkaMessageConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${calculator.request.topic}", groupId = "calculator")
    public void listen(ConsumerRecord<String, OperationKafkaRequest> record) {
        OperationKafkaRequest req = record.value();
        Headers headers = record.headers();

        BigDecimal a = req.getA();
        BigDecimal b = req.getB();
        String op = req.getOperation();
        BigDecimal result;

        logger.info("Received request: {} {} {} = ?", a, op, b);

        switch (op) {
            case "add":
                result = a.add(b);
                break;
            case "subtract":
                result = a.subtract(b);
                break;
            case "multiply":
                result = a.multiply(b);
                break;
            case "divide":
                result = b.compareTo(BigDecimal.ZERO) != 0
                        ? a.divide(b, 10, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO;
                break;
            default:
                result = BigDecimal.ZERO;
        }

        logger.info("Result: {}", result);

        // Extract correlation ID
        Header correlationHeader = headers.lastHeader(KafkaHeaders.CORRELATION_ID);
        byte[] correlationId = correlationHeader != null ? correlationHeader.value() : null;

        // Send back the response with the same correlation ID
        org.apache.kafka.clients.producer.ProducerRecord<String, Object> responseRecord =
                new org.apache.kafka.clients.producer.ProducerRecord<>(responseTopic, result);
        if (correlationId != null) {
            responseRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId);
        }

        logger.info("Sending response with correlation ID: {}", correlationId);

        kafkaTemplate.send(responseRecord);
    }
}
