package com.nubank.domain.strategy;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BuyOperationStrategy implements OperationStrategy {

    public Rate execute(StockOrder order, StockState state) {
        state.updateState(order);
        return new Rate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }
}
