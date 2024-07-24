import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {

    public static List<CallDataRecord> parseData(String file) throws IOException {
        List<CallDataRecord> data = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(file));
        while(reader.ready()) {
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

    public static void generateReport(Path reportsDir, List<CallDataRecord> callDataRecords) {
        var phoneNumber = callDataRecords.get(0).getPhoneNumber();
        try(Writer printWriter = new FileWriter(reportsDir + "/" + phoneNumber + ".txt", StandardCharsets.UTF_8, true);
            BufferedWriter writer = new BufferedWriter(printWriter)
        ) {
            double totalPrice = 0;    // Общая цена всех звонков
            double globalTime = 0;    // Общее время звонков за тарифный период в секундах
            writeReportHeader(writer, callDataRecords.get(0));

            for (CallDataRecord callDataRecord: callDataRecords) {
                double price = 0;
                long duration = Duration.between(callDataRecord.getStartTime(), callDataRecord.getStopTime()).toSeconds();
                switch (callDataRecord.getTariffType()) {
                    case PER_MINUTE:
                        price = (double) duration/60 * 1.5;
                        totalPrice += price;
                        break;
                    case COMMON:
                        if (callDataRecord.getCallType() == CallType.INBOX) {
                            globalTime -= duration;
                        } else {
                            if (globalTime /60 <= 100) {
                                price = (double) duration/60 * 0.5;
                            } else {
                                price = (double) duration/60 * 1;
                            }
                            totalPrice += price;
                        }
                        break;
                    case UNLIMITED:
                        // Если потрачено менее 300 минут
                        if (globalTime /60 <= 300) {
                            totalPrice = 100;
                        }

                        // Если звонок происходит после израсходования 300 минут
                        if (globalTime /60 > 300) {
                            price = (double) duration/60;
                            totalPrice += price;
                        }

                        // Если во время данного звонка был преодолён порог в 300 минут
                        if ((globalTime - duration)/60 <= 300 && globalTime /60 > 300) {
                            price = (globalTime / 60) - 300;
                            totalPrice = (double) 100 + price;
                        }
                        break;
                }
                writeLine(writer, callDataRecord, duration, price);
            }
            writeReportFooter(writer, totalPrice);
        } catch (IOException e) {
            System.out.println("Can't write report to file: file=src/main/resources/reports/" + phoneNumber + ".txt");
            e.printStackTrace(System.out);
        }
    }

    private static void writeReportHeader(BufferedWriter writer, CallDataRecord callDataRecord) throws IOException {
        writer.write("Tariff index: " + callDataRecord.getTariffType().code + "\n");
        writer.write("----------------------------------------------------------------------------\n");
        writer.write("Report for phone number " + callDataRecord.getPhoneNumber() + ":\n");
        writer.write("----------------------------------------------------------------------------\n");
        writer.write("| Call Type |     Start Time      |     End Time        | Duration | Cost  |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    private static void writeLine(BufferedWriter writer, CallDataRecord callDataRecord, long duration, double price) throws IOException {
        var formattedDuration = String.format("%02d:%02d:%02d", duration / 3600, duration / 60 % 60, duration % 60);
        writer.write("|     " + callDataRecord.getCallType().code + "    | " + formatDate(callDataRecord.getStartTime()) + " | " + formatDate(callDataRecord.getStopTime()) + " | " + formattedDuration + " |  " + String.format("%(.2f", price).replace(",", ".") + " |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    private static void writeReportFooter(BufferedWriter writer, double totalPrice) throws IOException {
        writer.write("|                                           Total Cost: |     " + String.format("%(.2f", totalPrice).replace(",", ".") + " rubles |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }
}
