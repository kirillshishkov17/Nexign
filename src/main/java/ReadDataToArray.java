import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadDataToArray {

    public static ArrayList<String> readData(String fileName) {
        ArrayList<String> data = new ArrayList<>();

        try {
            // Инструменты для чтения файла
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            // Читаем каждую строку и записываем её в массив
            while (line != null) {
                data.add(line);
                line = reader.readLine();
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }
}
