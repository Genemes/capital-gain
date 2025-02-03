package com.nubank.domain.strategy;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;

public interface OperationStrategy {
    Rate execute(StockOrder order, StockState state);
}
