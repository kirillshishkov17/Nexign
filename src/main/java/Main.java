import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<String> data;

        // Читаем данные из файла и записываем в List
        try {
            data = Utils.readData("src/main/resources/cdr.txt");

            // Создаёт папку reports
            File reportsDir = new File("src/main/resources/reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            // Получает уникальный набор номеров
            List<String> phoneNumbers = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                String[] elements = ReportGenerator.stringToArrayOfStrings(data, i);
                phoneNumbers.add(elements[1]);
            }
            Set<String> uniqPhoneNumbers = new HashSet<>(phoneNumbers);

            // Находит совпадения по номеру телефона и генерирует отчёт
            for (String number : uniqPhoneNumbers) {
                ReportGenerator.generate(data, number);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
