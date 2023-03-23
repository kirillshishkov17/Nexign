import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReportGenerator {

    private static int globalTime = 0;            // Общее время звонков за тарифный период в секундах
    private static double totalPrice;             // Общая стоимость звонков за тарифный период
    private static String tariff;                 // Тариф абонента

    public static void generate(List<String> data, String phoneNumber) {

        // Получение данных определённого абонента
        ArrayList<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(".{4}" + phoneNumber + ".+");

        for (String s : data) {
            if (p.matcher(s).matches()) {
                matches.add(s);
            }
        }

        // Получение тарифа абонента
        tariff = stringToArrayOfStrings(matches, 0)[4];

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
            globalTime += duration;

            // Получение стоимости звонка
            double price = 0;

            if (tariff.equals("06")) {
                // Если потрачено менее 300 минут
                if (globalTime/60 <= 50) {
                    price = 0;
                }

                // Если во время данного звонка был преодалён порог в 300 минут
                if ((globalTime - duration)/60 <= 50 && globalTime/60 > 50) {
                    price = globalTime/60 - 50;
                }

                // Если звонок происходит после израсходования 300 минут
                if (globalTime/60 > 50) {
                    price = (double) duration/60;
                }
            }
            System.out.println(price);

//            // Блок проверки
//            System.out.println("Start: " + startTime);
//            System.out.println("End: " + endTime);
//            System.out.println(duration);
        }
    }

    private static String[] stringToArrayOfStrings(ArrayList<String> array, int index) {
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
        LocalDateTime time = LocalDateTime.of(localDate, localTime);
        return time;
    }
}
