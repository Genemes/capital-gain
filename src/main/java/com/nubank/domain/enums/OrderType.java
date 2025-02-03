package com.nubank.domain.enums;

public enum OrderType {
    BUY("buy"),
    SELL("sell");

    private final String operation;

    OrderType(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public static OrderType fromString(String value) {
        return OrderType.valueOf(value.toUpperCase());
    }

}
