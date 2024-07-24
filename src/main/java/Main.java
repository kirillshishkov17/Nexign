import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            // создаёт папку reports
            Path reportsDir = Paths.get("src/main/resources/reports");
            if (Files.notExists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }

            // читает данные и маппит в массив объектов
            List<CallDataRecord> callDataRecords = Utils.parseData("src/main/resources/cdr.txt");

            // получает набор уникальных номеров телефонов
            Set<String> uniqPhoneNumbers = Utils.getUniqPhoneNumbers(callDataRecords);

            //todo Удалить. Работает с массивом строк. Новая реализация работает с объектами
            List<String> data = Utils.readData("src/main/resources/cdr.txt");

            // Находит совпадения по номеру телефона и генерирует отчёт
            //todo Переработать метод
            for (String number : uniqPhoneNumbers) {
                ReportGenerator.generate(data, number);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
