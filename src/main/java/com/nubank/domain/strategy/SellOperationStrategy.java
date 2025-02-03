package com.nubank.domain.strategy;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SellOperationStrategy implements OperationStrategy {

    private final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public Rate execute(StockOrder order, StockState state) {
        state.updateState(order);
        BigDecimal tax = calculateTax(order, state);
        return new Rate(tax);
    }

    private BigDecimal calculateTax(StockOrder order, StockState state) {
        BigDecimal operationValue = order.operationResult(state.getAveragePrice());

        if (isOperationLoss(operationValue)) {
            updateLoss(state, state.getLoss().subtract(operationValue));
            return ZERO;
        }

        if (order.isExceedsLimit()) {
            return processTaxDeduction(operationValue, state)
                    .divide(new BigDecimal(5), 2, RoundingMode.HALF_UP);
        }

        return ZERO;
    }

    private boolean isOperationLoss(BigDecimal valueOperation) {
        return valueOperation.compareTo(ZERO) < 0;
    }

    private BigDecimal processTaxDeduction(BigDecimal value, StockState state) {
        if (value.compareTo(state.getLoss()) >= 0) {
            BigDecimal deduction = state.getLoss();
            updateLoss(state, ZERO);
            return value.subtract(deduction);
        } else {
            updateLoss(state, state.getLoss().subtract(value));
            return ZERO;
        }
    }

    public void updateLoss(StockState state, BigDecimal amount) {
        state.setLoss(amount);
    }
}
