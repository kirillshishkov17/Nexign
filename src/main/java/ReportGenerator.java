import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReportGenerator {

    public static void generate(List<String> data, String phoneNumber) throws IOException {
        double totalPrice = 0;
        // Общее время звонков за тарифный период в секундах
        double globalTime = 0;

        // Получение данных определённого абонента
        ArrayList<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(".{4}" + phoneNumber + ".+");

        for (String s : data) {
            if (p.matcher(s).matches()) {
                matches.add(s);
            }
        }

        // Получение тарифа абонента
        String tariff = stringToArrayOfStrings(matches, 0)[4];

        // Начало создания файла отчёта
            PrintWriter writer = new PrintWriter("./reports/" + phoneNumber + ".txt", StandardCharsets.UTF_8);
            writer.println("Tariff index: " + tariff);
            writer.println("----------------------------------------------------------------------------");
            writer.println("Report for phone number " + phoneNumber + ":");
            writer.println("----------------------------------------------------------------------------");
            writer.println("| Call Type |   Start Time        |     End Time        | Duration | Cost  |");
            writer.println("----------------------------------------------------------------------------");



        for (int i = 0; i < matches.size(); i++) {
            String[] elements = stringToArrayOfStrings(matches, i);

            // Получение типа звонка
            String callType = elements[0];

            // Получение времени начала звонка
            LocalDateTime startTime = convertDate(elements[2]);
            String startTimeString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(startTime);

            // Получение времени окончания звонка
            LocalDateTime endTime = convertDate(elements[3]);
            String endTimeString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(endTime);

            // Получение длительности звонка
            long duration = Duration.between(startTime, endTime).toSeconds();
            String durationString = String.format("%02d:%02d:%02d", duration / 3600, duration / 60 % 60, duration % 60);
            globalTime += duration;

            // Получение стоимости звонка
            double price = 0;

            if (tariff.equals("06")) {
                // Если потрачено менее 300 минут
                if (globalTime /60 <= 300) {
                    price = 0;
                    totalPrice = 100;
                }

                // Если звонок происходит после израсходования 300 минут
                if (globalTime /60 > 300) {
                    price = (double) duration/60;
                    totalPrice += price;
                }

                // Если во время данного звонка был преодалён порог в 300 минут
                if ((globalTime - duration)/60 <= 300 && globalTime /60 > 300) {
                    price = (globalTime / 60) - 300;
                    totalPrice = (double) 100 + price;
                }
            }

            if (tariff.equals("03")) {
                price = (double) duration/60 * 1.5;
                totalPrice += price;
            }

            if (tariff.equals("11")) {
                if (callType.equals("02")) {
                    globalTime -= duration;
                    price = 0;
                } else {
                    if (globalTime /60 <= 100) {
                        price = (double) duration/60 * 0.5;
                    } else {
                        price = (double) duration/60 * 1;
                    }
                    totalPrice += price;
                }
            }
            writer.println("|     "+ callType +"    | "+ startTimeString +" | "+ endTimeString + " | " + durationString + " |  "
                    + String.format("%(.2f", price).replace(",", ".") +" |");
            writer.println("----------------------------------------------------------------------------");
        }

        writer.println("|                                           Total Cost: |     " + String.format("%(.2f", totalPrice).replace(",", ".") + " rubles |");
        writer.println("----------------------------------------------------------------------------");
        writer.close();
    }

    public static String[] stringToArrayOfStrings(ArrayList<String> array, int index) {
        // Разбиваю строку на массив элементов
        String[] elements = array.get(index).split("\\s+");
        // Убираем знаки препинания
        for (int j = 0; j < elements.length; j++) {
            elements[j] = elements[j].replaceAll("\\W", "");
        }
        return elements;
    }

    private static LocalDateTime convertDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalTime localTime = LocalTime.parse(date, formatter);
        return LocalDateTime.of(localDate, localTime);
    }
}
