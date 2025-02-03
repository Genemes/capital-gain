package service;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;
import com.nubank.domain.enums.OrderType;
import com.nubank.service.StockTaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockTaxCalculatorServiceUnitTest {

    private StockTaxCalculatorService taxCalculatorService;
    private StockState stockState;

    @BeforeEach
    void setUp() {
        stockState = new StockState();
        taxCalculatorService = new StockTaxCalculatorService(stockState);
    }

    @Test
    void testCalculateBuyOrder_NoTax() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("50.00"), 100);
        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder));

        assertEquals(1, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.get(0).tax());
    }

    @Test
    void testCalculateSellOrder_NoProfit_NoTax() {
        stockState.setAveragePrice(new StockOrder(OrderType.BUY, new BigDecimal("50.00"), 100));
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 100);

        List<Rate> rates = taxCalculatorService.calculate(List.of(sellOrder));

        assertEquals(1, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
    }

    @Test
    void testCalculateSellOrder_WithProfit_TaxableAboveLimit() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 500);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 500);

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder,sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
        assertEquals(new BigDecimal("1000.00"), rates.get(1).tax());
    }

    @Test
    void testCalculateSellOrder_WithProfit_BelowTaxLimit() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 300);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("50.00"), 300);

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder,sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
        assertEquals(new BigDecimal("0.00"), rates.get(1).tax());
    }

    @Test
    void testCalculateSellOrder_WithLoss() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 100);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("40.00"), 100);

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder,sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.get(0).tax());
        assertEquals(new BigDecimal("0.00"), stockState.getLoss());
    }

    @Test
    void testCalculateSellOrder_WithTaxDeduction() {
        StockOrder buyOrder = new StockOrder(OrderType.BUY, new BigDecimal("40.00"), 500);
        StockOrder sellOrder = new StockOrder(OrderType.SELL, new BigDecimal("60.00"), 500);
        stockState.setLoss(new BigDecimal("500.00"));

        List<Rate> rates = taxCalculatorService.calculate(List.of(buyOrder,sellOrder));

        assertEquals(2, rates.size());
        assertEquals(new BigDecimal("0.00"), rates.getFirst().tax());
        assertEquals(new BigDecimal("1900.00"), rates.get(1).tax());
        assertEquals(new BigDecimal("0.00"), stockState.getLoss());
    }
}
