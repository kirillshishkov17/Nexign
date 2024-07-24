import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {

    @Deprecated
    public static List<String> readData(String filePath) throws IOException {
        List<String> data = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(filePath));
        while(reader.ready()) {
            data.add(reader.readLine());
        }
        reader.close();
        return data;
    }

    public static Set<String> getUniqPhoneNumbers_old(List<String> data) {
        Set<String> set = new HashSet<>();
        for (String string: data) {
            set.add(string.substring(4, 15));
        }
        return set;
    }

    public static List<CallDataRecord> parseData(String file) throws IOException {
        //todo считывать строку из BufferedReader
        List<CallDataRecord> data = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(file));
        while(reader.ready()) {
            // готовит данные
            String[] parts = reader.readLine().split(", ");
            var callType = CallType.fromCode(parts[0]);
            var phoneNumber = parts[1];
            var startTime = covertStringToDate(parts[2]);
            var stopTime = covertStringToDate(parts[3]);
            var tariffType = TariffType.fromCode(parts[4]);
            data.add(new CallDataRecord(callType, phoneNumber, startTime, stopTime, tariffType));
        }
        return data;
    }

    private static LocalDateTime covertStringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalTime localTime = LocalTime.parse(date, formatter);
        return LocalDateTime.of(localDate, localTime);
    }

    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(outputFormatter);
    }

    public static Set<String> getUniqPhoneNumbers(List<CallDataRecord> data) {
        Set<String> set = new HashSet<>();
        for (CallDataRecord callDataRecord: data) {
            set.add(callDataRecord.getPhoneNumber());
        }
        return set;
    }
}
