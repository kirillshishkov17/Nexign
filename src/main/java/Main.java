import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            Path reportsDir = Files.createDirectories(Paths.get("reports"));
            var callDataRecords = ReportService.parseFile("src/main/resources/cdr.txt");
            ReportService.generate(reportsDir, callDataRecords);
        } catch (IOException e) {
            System.out.println("Hello world!");
            //todo обработать исключение
        }
    }
}
