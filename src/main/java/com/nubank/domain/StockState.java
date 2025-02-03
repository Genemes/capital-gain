package com.nubank.domain;

import com.nubank.domain.enums.OrderType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockState {
    private BigDecimal averagePrice;
    private int quantity;
    private BigDecimal loss;

    public StockState() {
        this.averagePrice = new BigDecimal(0);
        this.quantity = 0;
        this.loss = new BigDecimal(0);
    }

    public void updateState(StockOrder stockOrder) {
        this.setAveragePrice(stockOrder);
        this.setQuantity(stockOrder);
    }

    public BigDecimal getAveragePrice() {
        return this.averagePrice;
    }

    public void setAveragePrice(StockOrder stockOrder) {
        if(stockOrder.operation() == OrderType.BUY) {
            var cashOnHand = this.averagePrice.multiply(new BigDecimal(this.quantity));
            var operationValue = stockOrder.calculateSalePrice();
            var totalQuantity = new BigDecimal(this.quantity + stockOrder.quantity());
            this.averagePrice = (cashOnHand.add(operationValue)).divide(totalQuantity, 2, RoundingMode.HALF_UP);
        }
    }

    public void setQuantity(StockOrder stockOrder) {
        this.quantity = stockOrder.operation() == OrderType.BUY
                ? this.quantity + stockOrder.quantity()
                : this.quantity - stockOrder.quantity();
    }

    public BigDecimal getLoss() {
        return this.loss.setScale(2, RoundingMode.HALF_UP);
    }

    public void setLoss(BigDecimal loss) {
        this.loss = loss;
    }

    @Override
    public String toString() {
        return """
           StockState {
               averagePrice = %s,
               quantity = %d,
               loss = %s
           }
           """.formatted(this.averagePrice, this.quantity, this.loss);
    }

}
