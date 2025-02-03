package com.nubank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.domain.StockState;
import com.nubank.service.StockTaxCalculatorService;
import com.nubank.utils.StockOrderDeserializer;

import java.util.List;
import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) {
        String json = "[{\"operation\":\"buy\", \"unit-cost\":20.00, \"quantity\":10}, " +
                "{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\":5},"+
                "{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\":5}]";

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StockOrder.class, new StockOrderDeserializer());
        Gson gson = gsonBuilder.create();

        try {
            Type listType = new TypeToken<List<StockOrder>>() {}.getType();
            List<StockOrder> stockOrders = gson.fromJson(json, listType);

            StockState stockState = new StockState();
            StockTaxCalculatorService service = new StockTaxCalculatorService(stockState);
            List<Rate> rates = service.calculate(stockOrders);
            for (Rate rate : rates) {
                System.out.println(rate);
            }

            System.out.println("Received JSON: " + gson.toJson(stockOrders));
        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao converter JSON: " + e.getMessage());
        }
    }
}