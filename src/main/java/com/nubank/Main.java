package com.nubank;

import com.nubank.service.JsonService;
import com.nubank.utils.JsonInputReader;

import java.util.List;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cole cada lista JSON (uma por linha) e pressione Enter para processar. Digite 'exit' para sair.");

        JsonService jsonProcessor = new JsonService();

        while (true) {
            System.out.println("Cole o JSON e pressione Enter para processar, ou digite 'exit' para sair");
            List<String> jsonLines = JsonInputReader.readJsonLines(scanner);

            if (jsonLines.getFirst().equalsIgnoreCase("exit")) {
                System.out.println("Encerrando o programa.");
                break;
            }

            for (String json : jsonLines) {
                if (!json.isEmpty()) {
                    jsonProcessor.processStockOrdersFromJson(json);
                }
            }
        }

        scanner.close();
    }
}