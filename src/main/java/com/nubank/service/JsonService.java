package com.nubank.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nubank.domain.Rate;
import com.nubank.domain.StockOrder;
import com.nubank.utils.StockOrderDeserializer;

import java.lang.reflect.Type;
import java.util.List;

public class JsonService {

    private static final Gson gson = configureGson();
    private final StockTaxCalculatorService service = new StockTaxCalculatorService();

    public void processStockOrdersFromJson(String json) {
        try {
            List<StockOrder> orders = deserializeJsonToStockOrders(json);
            List<Rate> rates = service.calculate(orders);

            System.out.println(gson.toJson(rates));
        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao converter JSON: " + e.getMessage());
        }
    }

    private static Gson configureGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StockOrder.class, new StockOrderDeserializer());
        return gsonBuilder.create();
    }

    private List<StockOrder> deserializeJsonToStockOrders(String json) {
        Type listType = new TypeToken<List<StockOrder>>() {}.getType();
        return gson.fromJson(json, listType);
    }

}