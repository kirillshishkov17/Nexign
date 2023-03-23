import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Читает данные из файла и записываем в List
        List<String> data = ReadDataToArray.readData("src/main/resources/cdr.txt");

        // Создаёт папку reports
        File theDir = new File("reports");
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        // Находит совпадения по номеру телефона и генерирует отчёт
        try {
            ReportGenerator.generate(data, "70785395867");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
