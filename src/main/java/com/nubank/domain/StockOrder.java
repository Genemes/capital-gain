package com.nubank.domain;

import com.nubank.domain.enums.OrderType;

import java.math.BigDecimal;

    public record StockOrder (OrderType operation, BigDecimal unitCost, int quantity) {

        private static final BigDecimal TAX_LIMIT = new BigDecimal(20000);

        public BigDecimal calculateSalePrice() {
            return unitCost.multiply(new BigDecimal(quantity));
        }

        public boolean isExceedsLimit() {
            return calculateSalePrice().compareTo(TAX_LIMIT) > 0;
        }

        public BigDecimal operationResult(BigDecimal averagePrive) {
            return calculateSalePrice()
                    .subtract(averagePrive.multiply(new BigDecimal(quantity)));
        }
    }
