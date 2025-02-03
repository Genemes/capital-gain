package service;

import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;
import com.nubank.domain.enums.OrderType;
import com.nubank.domain.strategy.BuyOperationStrategy;
import com.nubank.domain.strategy.OperationStrategy;
import com.nubank.domain.strategy.SellOperationStrategy;
import com.nubank.service.StockTaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StockTaxCalculatorServiceIntegrationTest {

    private StockTaxCalculatorService service;
    private Map<OrderType, OperationStrategy> strategies;

    @BeforeEach
    void setUp() {
        StockState stockState = new StockState();
        Map<OrderType, OperationStrategy> strategies = new HashMap<>();
        strategies.put(OrderType.BUY, new BuyOperationStrategy());
        strategies.put(OrderType.SELL, new SellOperationStrategy());
        service = new StockTaxCalculatorService(stockState, strategies);
    }

    @Test
    void scenario1_ShouldReturnZeroTaxes() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 100),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 50),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 50)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
    }

    @Test
    void scenario2_ShouldReturnTaxesForProfitSale() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(5.00), 5000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(10000.00).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
    }

    @Test
    void scenario3_ShouldReturnCorrectTaxValues() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 100), // +1.000
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 50), // -750   | 250 (lucro) | 0 (tax)
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 50),
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000), // + 100.000
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 5000), // - 100.000 | (lucro) | 20.000 (tax)
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(5.00), 5000)   // - 25000
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(3).tax());
        assertEquals(BigDecimal.valueOf(10000.00).setScale(2, RoundingMode.HALF_UP), rates.get(4).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(5).tax());
    }

    @Test
    void scenario4_ShouldCalculateTaxesCorrectly() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(5.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 3000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
    }

    @Test
    void scenario5_ShouldReturnZeroTaxesForEqualCosts() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(25.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 10000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
    }

    @Test
    void scenario6_ShouldReturnCorrectTaxValuesForTwoSells() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(25.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(25.00), 5000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
        assertEquals(BigDecimal.valueOf(10000.00).setScale(2, RoundingMode.HALF_UP), rates.get(3).tax());
    }

    @Test
    void scenario7_ShouldHandleLossAndProfitTaxation() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(2.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 2000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 2000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(25.00), 1000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(3).tax());
        assertEquals(BigDecimal.valueOf(3000.00).setScale(2, RoundingMode.HALF_UP), rates.get(4).tax());
    }

    @Test
    void scenario8_ShouldHandleMultipleOperationsWithProfitAndLoss() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(2.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 2000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(20.00), 2000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(25.00), 1000),
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(20.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(15.00), 5000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(30.00), 4350),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(30.00), 650)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(3).tax());
        assertEquals(BigDecimal.valueOf(3000.00).setScale(2, RoundingMode.HALF_UP), rates.get(4).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(5).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(6).tax());
        assertEquals(BigDecimal.valueOf(3700.00).setScale(2, RoundingMode.HALF_UP), rates.get(7).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(8).tax());
    }

    @Test
    void scenario9_ShouldReturnCorrectTaxValuesForHighProfit() {
        List<StockOrder> stockOrders = List.of(
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(10.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(50.00), 10000),
                new StockOrder(OrderType.BUY, BigDecimal.valueOf(20.00), 10000),
                new StockOrder(OrderType.SELL, BigDecimal.valueOf(50.00), 10000)
        );
        List<Rate> rates = service.calculate(stockOrders);
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(0).tax());
        assertEquals(BigDecimal.valueOf(80000.00).setScale(2, RoundingMode.HALF_UP), rates.get(1).tax());
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), rates.get(2).tax());
        assertEquals(BigDecimal.valueOf(60000.00).setScale(2, RoundingMode.HALF_UP), rates.get(3).tax());
    }
}
