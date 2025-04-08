package com.example.rest.controller;

import com.example.rest.dto.OperationKafkaRequest;
import com.example.rest.dto.OperationRequest;
import com.example.rest.dto.OperationResponse;
import com.example.rest.kafka.KafkaMessageProducer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public static class AddRequest {
        private Integer num1;
        private Integer num2;

        
        public Integer getNum1() {
            return num1;
        }

        public void setNum1(Integer num1) {
            this.num1 = num1;
        }

        public Integer getNum2() {
            return num2;
        }

        public void setNum2(Integer num2) {
            this.num2 = num2;
        }
    }


    public ResponseEntity<?> add(@RequestBody AddRequest request) {
        try {
           
            if (request.getNum1() == null || request.getNum2() == null) {
                return ResponseEntity.badRequest().body("Invalid input");
            }

            int result = request.getNum1() + request.getNum2();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request");
        }
    }
}


