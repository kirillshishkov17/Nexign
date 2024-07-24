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

            // читает данные и маппит в массив объектов
            List<CallDataRecord> callDataRecords = Utils.parseData("src/main/resources/cdr.txt");

            // получает набор уникальных номеров телефонов
            Set<String> uniqPhoneNumbers = Utils.getUniqPhoneNumbers(callDataRecords);

            // генерирует отчёт по каждому номеру телефона
            for (var number : uniqPhoneNumbers) {
                List<CallDataRecord> filteredRecords = callDataRecords.stream()
                        .filter(record -> record.getPhoneNumber().equals(number))
                        .sorted(Comparator.comparing(CallDataRecord::getStartTime))
                        .collect(Collectors.toList());

                Utils.generateReport(reportsDir, filteredRecords);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
