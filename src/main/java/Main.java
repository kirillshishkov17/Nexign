import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Path reportsDir = Files.createDirectories(Path.of("reports"));
            var callDataRecords = ReportService.parseFile(Path.of("src/main/resources/cdr.txt"));
            ReportService.generate(reportsDir, callDataRecords);
        } catch (IOException e) {
            //todo обработать исключение
        }
    }
}
