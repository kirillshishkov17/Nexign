import Entity.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private static final DateTimeFormatter DATE_PARSE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATE_REPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<CallDataRecord> parseFile(String file) throws IOException {
        List<CallDataRecord> data = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",\\s*");
            var callType = CallType.fromCode(parts[0]);
            var phoneNumber = parts[1];
            var startTime = LocalDateTime.parse(parts[2], DATE_PARSE_FORMATTER);
            var stopTime = LocalDateTime.parse(parts[3], DATE_PARSE_FORMATTER);
            var tariffType = TariffType.fromCode(parts[4]);
            data.add(new CallDataRecord(callType, phoneNumber, startTime, stopTime, tariffType));
        }
        return data;
    }

    public static void generate(Path reportsDir, List<CallDataRecord> callDataRecords) throws IOException {
        Map<String, PhoneNumberReportData> phoneNumberReportData = new HashMap<>();
        for (CallDataRecord record : callDataRecords) {
            var phoneData = phoneNumberReportData.get(record.getPhoneNumber());
            BufferedWriter writer;
            if (phoneData != null) {
                writer = phoneData.getBufferedWriter();
                var cdrPriceAndDuration = calcPriceAndDuration(record);
                writeLine(writer, record, cdrPriceAndDuration.duration(), cdrPriceAndDuration.price());
                phoneData.setTotalCosts(phoneData.getTotalCosts() + calcPriceAndDuration(record).price());
                phoneData.setTotalTime(phoneData.getTotalTime() + calcPriceAndDuration(record).duration());
            } else {
                writer = new BufferedWriter(new FileWriter(reportsDir + "/" + record.getPhoneNumber() + ".txt", StandardCharsets.UTF_8, true));
                writeReportHeader(writer, record);
                var priceAndDuration = calcPriceAndDuration(record);
                writeLine(writer, record, priceAndDuration.duration(), priceAndDuration.price());
                phoneNumberReportData.put(record.getPhoneNumber(), new PhoneNumberReportData(priceAndDuration.price(), priceAndDuration.duration(), writer));
            }
        }
        for (Map.Entry<String, PhoneNumberReportData> phoneNumberData : phoneNumberReportData.entrySet()) {
            var writer = phoneNumberData.getValue().getBufferedWriter();
            var totalCosts = phoneNumberData.getValue().getTotalCosts();
            writeReportFooter(writer, totalCosts);
            writer.close();
        }
    }

    public static void generateReport(Path reportsDir, List<CallDataRecord> callDataRecords) {
        var phoneNumber = callDataRecords.get(0).getPhoneNumber();
        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(reportsDir + "/" + phoneNumber + ".txt", StandardCharsets.UTF_8, true))
        ) {
            double totalPrice = 0;    // Общая цена всех звонков
            double globalTime = 0;    // Общее время звонков за тарифный период в секундах
            writeReportHeader(writer, callDataRecords.get(0));

            for (CallDataRecord callDataRecord : callDataRecords) {
                double price = 0;
                long duration = Duration.between(callDataRecord.getStartTime(), callDataRecord.getStopTime()).toSeconds();
                switch (callDataRecord.getTariffType()) {
                    case PER_MINUTE:
                        price = (double) duration / 60 * 1.5;
                        totalPrice += price;
                        break;
                    case COMMON:
                        if (callDataRecord.getCallType() == CallType.INBOX) {
                            globalTime -= duration;
                        } else {
                            if (globalTime / 60 <= 100) {
                                price = (double) duration / 60 * 0.5;
                            } else {
                                price = (double) duration / 60 * 1;
                            }
                            totalPrice += price;
                        }
                        break;
                    case UNLIMITED:
                        // Если потрачено менее 300 минут
                        if (globalTime / 60 <= 300) {
                            totalPrice = 100;
                        }

                        // Если звонок происходит после израсходования 300 минут
                        if (globalTime / 60 > 300) {
                            price = (double) duration / 60;
                            totalPrice += price;
                        }

                        // Если во время данного звонка был преодолён порог в 300 минут
                        if ((globalTime - duration) / 60 <= 300 && globalTime / 60 > 300) {
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
        writer.write("|     " + callDataRecord.getCallType().code + "    | " + callDataRecord.getStartTime().format(DATE_REPORT_FORMATTER) + " | " + callDataRecord.getStopTime().format(DATE_REPORT_FORMATTER) + " | " + formattedDuration + " |  " + String.format("%(.2f", price).replace(",", ".") + " |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    private static void writeReportFooter(BufferedWriter writer, double totalPrice) throws IOException {
        writer.write("|                                           Total Cost: |     " + String.format("%(.2f", totalPrice).replace(",", ".") + " rubles |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    public static PriceDurationDto calcPriceAndDuration(CallDataRecord cdr) {
        double price;
        long duration = Duration.between(cdr.getStartTime(), cdr.getStopTime()).toSeconds();
        price = switch (cdr.getTariffType()) {
            case PER_MINUTE -> (double) duration / 60 * 1.5;
            case COMMON ->
//                if (cdr.getCallType() == CallType.INBOX) {
//                    globalTime -= duration;
//                } else {
//                    if (globalTime / 60 <= 100) {
//                        price = (double) duration / 60 * 0.5;
//                    } else {
//                        price = (double) duration / 60 * 1;
//                    }
//                }
                    10;
            case UNLIMITED ->
//                // Если потрачено менее 300 минут
//                if (globalTime / 60 <= 300) {
//                    totalPrice = 100;
//                }
//
//                // Если звонок происходит после израсходования 300 минут
//                if (globalTime / 60 > 300) {
//                    price = (double) duration / 60;
//                    totalPrice += price;
//                }
//
//                // Если во время данного звонка был преодолён порог в 300 минут
//                if ((globalTime - duration) / 60 <= 300 && globalTime / 60 > 300) {
//                    price = (globalTime / 60) - 300;
//                    totalPrice = (double) 100 + price;
//                }
                    20;
        };

        return new PriceDurationDto(price, duration);
    }
}
