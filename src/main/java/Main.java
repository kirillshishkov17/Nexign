import Entity.CallDataRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            // создаёт папку reports
            Path reportsDir = Paths.get("reports");
            if (Files.notExists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }

            // читает данные в массив объектов
            var parsedData = Utils.parseFile("src/main/resources/cdr.txt");

            // получает набор уникальных номеров телефонов
            Set<String> uniquePhoneNumbers = parsedData.uniqueNumbers();

            // генерирует отчёт по каждому номеру телефона
            for (var number : uniquePhoneNumbers) {
                List<CallDataRecord> filteredRecords = parsedData.callDataRecords().stream()
                        .filter(record -> record.phoneNumber().equals(number))
                        .sorted(Comparator.comparing(CallDataRecord::startTime))
                        .collect(Collectors.toList());

                Utils.generateReport(reportsDir, filteredRecords);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
