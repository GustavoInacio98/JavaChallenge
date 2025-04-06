package com.example.calculator.dto;

import java.math.BigDecimal;

public class OperationKafkaRequest {
    private BigDecimal a;
    private BigDecimal b;
    private String operation;

    public OperationKafkaRequest() {}

    public OperationKafkaRequest(BigDecimal a, BigDecimal b, String operation) {
        this.a = a;
        this.b = b;
        this.operation = operation;
    }

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
        this.a = a;
    }

    public BigDecimal getB() {
        return b;
    }

    public void setB(BigDecimal b) {
        this.b = b;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
