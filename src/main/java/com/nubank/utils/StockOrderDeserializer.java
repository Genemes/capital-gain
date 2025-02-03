package com.nubank.utils;

import com.google.gson.*;
import com.nubank.domain.StockOrder;
import com.nubank.domain.enums.OrderType;

import java.lang.reflect.Type;
import java.math.BigDecimal;


public class StockOrderDeserializer implements JsonDeserializer<StockOrder> {

    @Override
    public StockOrder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        OrderType operation = OrderType.fromString(jsonObject.get("operation").getAsString());
        BigDecimal unitCost = jsonObject.get("unit-cost").getAsBigDecimal();
        int quantity = jsonObject.get("quantity").getAsInt();

        return new StockOrder(operation, unitCost, quantity);
    }
}