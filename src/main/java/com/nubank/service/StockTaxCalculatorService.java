package com.nubank.service;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StockTaxCalculatorService {

    private final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    StockState state;

    public StockTaxCalculatorService(StockState state) {
        this.state = state;
    }

    public List<Rate> calculate(List<StockOrder> stockOrders) {
        List<Rate> rates = new ArrayList<>();
        for (StockOrder stockOrder : stockOrders) {
            rates.add(executeOrder(stockOrder));
        }
        return rates;
    }

    private Rate executeOrder(StockOrder order) {
        state.updateState(order);

        return switch (order.operation()) {
            case BUY -> new Rate(ZERO);
            case SELL -> new Rate(taxAmount(order));
            default -> throw new IllegalArgumentException("Unknown operation: " + order.operation());
        };
    }

    private boolean isOperationLoss(BigDecimal valueOperation) {
        return valueOperation.compareTo(ZERO) < 0;
    }

    public BigDecimal taxAmount(StockOrder order) {
        var operationValue = order.operationResult(state.getAveragePrice());

        if(isOperationLoss(operationValue)) {
            updateLoss(state.getLoss().subtract(operationValue));
            return ZERO;
        }

        if(order.isExceedsLimit()) {
            return processTaxDeduction(operationValue)
                    .divide(new BigDecimal(5), 2, RoundingMode.HALF_UP);
        }

        return ZERO;
    }

    public BigDecimal processTaxDeduction(BigDecimal value) {
        if(value.compareTo(state.getLoss()) >= 0) {
            var deduction = state.getLoss();
            updateLoss(ZERO);
            return value.subtract(deduction);
        } else {
            updateLoss(state.getLoss().subtract(value));
            return ZERO;
        }
    }

    public void updateLoss(BigDecimal amount) {
        state.setLoss(amount);
    }

}
