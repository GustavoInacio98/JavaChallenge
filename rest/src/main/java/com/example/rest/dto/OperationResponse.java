package com.example.rest.dto;

import java.math.BigDecimal;

public class OperationResponse {
    private BigDecimal result;

    public OperationResponse(BigDecimal result) {
        this.result = result;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }
}
