package service;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;
import com.nubank.domain.enums.OrderType;
import com.nubank.domain.strategy.OperationStrategy;
import com.nubank.service.StockTaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockTaxCalculatorServiceUnitTest {

    private StockTaxCalculatorService taxCalculatorService;
    Map<OrderType, OperationStrategy> strategies;

    @BeforeEach
    void setUp() {
        taxCalculatorService = new StockTaxCalculatorService();
    }

    @Test
    void when_OnlyPurchaseTransaction_Expect_NoTax() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("50.00"), 100);
        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder));

        assertEquals(1, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
    }

    @Test
    void calculateSellOrder_NoProfit_Expect_NoTax() {
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 100);

        List<Rate> rates = taxCalculatorService.calculate(List.of(sellOrder));

        assertEquals(1, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
    }

    @Test
    void calculateSellOrder_WithProfitAboveTaxLimit_Expect_TaxApplied() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 500);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 500);

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder, sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
        assertEquals(new BigDecimal("1000.00"), rates.get(1).tax());
    }

    @Test
    void calculateSellOrder_WithProfitBelowTaxLimit_Expect_NoTax() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 300);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 300);

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder, sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
        assertEquals(new BigDecimal("0.00"), rates.get(1).tax());
    }

}
