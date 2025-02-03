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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Gson gson = createGson();
    private static final StockState stockState = new StockState();
    private static final StockTaxCalculatorService service = new StockTaxCalculatorService(stockState);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cole cada lista JSON e pressione Enter para processar. Pressione Ctrl+C para sair.");

        while (scanner.hasNextLine()) {
            String jsonChunk = scanner.nextLine().trim();

            if (!jsonChunk.isEmpty()) {
                processJsonChunk(jsonChunk);
            }
        }
    }

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StockOrder.class, new StockOrderDeserializer());
        return gsonBuilder.create();
    }

    private static void processJsonChunk(String jsonChunk) {
        try {
            List<StockOrder> stockOrders = parseJsonToStockOrders(jsonChunk);
            List<Rate> rates = service.calculate(stockOrders);
            printRates(rates);
        } catch (JsonSyntaxException e) {
            handleJsonError(e);
        }
    }

    private static List<StockOrder> parseJsonToStockOrders(String jsonChunk) {
        Type listType = new TypeToken<List<StockOrder>>() {}.getType();
        return gson.fromJson(jsonChunk, listType);
    }

    private static void printRates(List<Rate> rates) {
        System.out.println(gson.toJson(rates));
    }

    private static void handleJsonError(JsonSyntaxException e) {
        System.err.println("Erro ao converter JSON: " + e.getMessage());
    }
}
