package com.nubank.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsonInputReader {

    public static List<String> readJsonLines(Scanner scanner) {
        List<String> jsonLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                break;
            }

            jsonLines.add(line);
        }
        return jsonLines;
    }
}