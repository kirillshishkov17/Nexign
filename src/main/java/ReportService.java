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
        List<CallDataRecord> callDataRecords = new ArrayList<>();
        var reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",\\s*");
            var callType = CallType.fromCode(parts[0]);
            var phoneNumber = parts[1];
            var startTime = LocalDateTime.parse(parts[2], DATE_PARSE_FORMATTER);
            var stopTime = LocalDateTime.parse(parts[3], DATE_PARSE_FORMATTER);
            var tariffType = TariffType.fromCode(parts[4]);
            callDataRecords.add(new CallDataRecord(callType, phoneNumber, startTime, stopTime, tariffType));
        }
        return callDataRecords;
    }

    public static void generate(Path reportsDir, List<CallDataRecord> callDataRecords) throws IOException {
        Map<String, Subscriber> subscribers = new HashMap<>();

        for (CallDataRecord call : callDataRecords) {
            var subscriber = subscribers.get(call.phoneNumber());
            var callInfo = calcCostAndDuration(call, subscriber);
            BufferedWriter writer;

            if (subscriber != null) {
                writer = subscriber.getBufferedWriter();
                writeLine(writer, call, callInfo.duration(), callInfo.cost());
                subscriber.setTotalCost(subscriber.getTotalCost() + callInfo.cost());
                subscriber.setTotalTime(subscriber.getTotalTime() + callInfo.duration());
            } else {
                writer = new BufferedWriter(new FileWriter(reportsDir + "/" + call.phoneNumber() + ".txt", StandardCharsets.UTF_8, true));
                writeReportHeader(writer, call);
                writeLine(writer, call, callInfo.duration(), callInfo.cost());
                subscribers.put(call.phoneNumber(), new Subscriber(callInfo.cost(), callInfo.duration(), writer));
            }
        }

        for (Map.Entry<String, Subscriber> data : subscribers.entrySet()) {
            var writer = data.getValue().getBufferedWriter();
            var totalCost = data.getValue().getTotalCost();
            writeReportFooter(writer, totalCost);
            writer.close();
        }
    }

    public static CallInfoDto calcCostAndDuration(CallDataRecord cdr, Subscriber subscriber) {
        var totalTimeMinutes = (subscriber != null) ? (int) (subscriber.getTotalTime() / 60) : 0;
        long durationSec = Duration.between(cdr.startTime(), cdr.stopTime()).toSeconds();
        long durationMinutes = Duration.between(cdr.startTime(), cdr.stopTime()).toMinutes();
        double cost = 0;

        switch (cdr.tariffType()) {
            case PER_MINUTE -> cost = durationMinutes * 1.5;
            case COMMON -> {
                if (cdr.callType() == CallType.OUTBOX) {
                    if (totalTimeMinutes > 100) {
                        cost = durationMinutes * 1.5;
                    } else if (totalTimeMinutes + durationMinutes > 100) {
                        int remainingMinutes = 100 - totalTimeMinutes;
                        cost = remainingMinutes * 0.5 + (durationMinutes - remainingMinutes) * 1.5;
                    } else if (totalTimeMinutes + durationMinutes < 100) {
                        cost = durationMinutes * 0.5;
                    }
                }
            }
            case UNLIMITED -> {
                if (subscriber == null) {
                    cost = 100;
                } else if (totalTimeMinutes > 300) {
                    cost = durationMinutes;
                } else if (totalTimeMinutes + durationMinutes > 300) {
                    cost = (totalTimeMinutes + durationMinutes - 300);
                }
            }
        }

        return new CallInfoDto(cost, durationSec);
    }

    private static void writeReportHeader(BufferedWriter writer, CallDataRecord callDataRecord) throws IOException {
        writer.write("Tariff index: " + callDataRecord.tariffType().code + "\n");
        writer.write("----------------------------------------------------------------------------\n");
        writer.write("Report for phone number " + callDataRecord.phoneNumber() + ":\n");
        writer.write("----------------------------------------------------------------------------\n");
        writer.write("| Call Type |     Start Time      |     End Time        | Duration | Cost  |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    private static void writeLine(BufferedWriter writer, CallDataRecord callDataRecord, long duration, double price) throws IOException {
        var formattedDuration = String.format("%02d:%02d:%02d", duration / 3600, duration / 60 % 60, duration % 60);
        writer.write("|     " + callDataRecord.callType().code + "    | " + callDataRecord.startTime().format(DATE_REPORT_FORMATTER) + " | " + callDataRecord.stopTime().format(DATE_REPORT_FORMATTER) + " | " + formattedDuration + " |  " + String.format("%(.2f", price).replace(",", ".") + " |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }

    private static void writeReportFooter(BufferedWriter writer, double totalPrice) throws IOException {
        writer.write("|                                           Total Cost: |     " + String.format("%(.2f", totalPrice).replace(",", ".") + " rubles |\n");
        writer.write("----------------------------------------------------------------------------\n");
    }
}
