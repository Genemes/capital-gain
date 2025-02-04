package com.nubank.service;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;
import com.nubank.domain.enums.OrderType;
import com.nubank.domain.strategy.BuyOperationStrategy;
import com.nubank.domain.strategy.OperationStrategy;
import com.nubank.domain.strategy.SellOperationStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockTaxCalculatorService {

    StockState state;
    Map<OrderType, OperationStrategy> strategies;

    public StockTaxCalculatorService() {
        this.state = new StockState();

        this.strategies = new HashMap<>();
        this.strategies.put(OrderType.BUY, new BuyOperationStrategy());
        this.strategies.put(OrderType.SELL, new SellOperationStrategy());
    }

    public List<Rate> calculate(List<StockOrder> stockOrders) {
        List<Rate> rates = new ArrayList<>();
        for (StockOrder stockOrder : stockOrders) {
            OperationStrategy strategy = strategies.get(stockOrder.operation());
            if (strategy != null) {
                rates.add(strategy.execute(stockOrder, state));
            } else {
                throw new IllegalArgumentException("Unknown operation: " + stockOrder.operation());
            }
        }
        return rates;
    }

}