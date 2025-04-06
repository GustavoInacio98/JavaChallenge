package com.example.rest.controller;

import com.example.rest.dto.OperationKafkaRequest;
import com.example.rest.dto.OperationRequest;
import com.example.rest.dto.OperationResponse;
import com.example.rest.kafka.KafkaMessageProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final KafkaMessageProducer producer;

    public CalculatorController(KafkaMessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/add")
    public OperationResponse add(@RequestBody OperationRequest req) throws Exception {
        return delegateKafka("add", req);
    }

    @PostMapping("/subtract")
    public OperationResponse subtract(@RequestBody OperationRequest req) throws Exception {
        return delegateKafka("subtract", req);
    }

    @PostMapping("/multiply")
    public OperationResponse multiply(@RequestBody OperationRequest req) throws Exception {
        return delegateKafka("multiply", req);
    }

    @PostMapping("/divide")
    public OperationResponse divide(@RequestBody OperationRequest req) throws Exception {
        return delegateKafka("divide", req);
    }

    private OperationResponse delegateKafka(String operation, OperationRequest req) throws Exception {
        OperationKafkaRequest message = new OperationKafkaRequest(req.getA(), req.getB(), operation);
        return new OperationResponse(producer.sendAndReceive(message));
    }
}


