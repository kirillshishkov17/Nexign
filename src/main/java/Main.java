import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Читаем данные из файла и записываем в List
        List<String> data = ReadDataToArray.readData("src/main/resources/cdr.txt");

        // Находим совпадения по строке
        ReportGenerator.generate(data, "71747641686");
    }
}
