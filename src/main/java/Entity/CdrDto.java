package Entity;

import java.util.List;
import java.util.Set;

/**
 * Данный класс является представлением данных из файла cdr.txt
 * @param callDataRecords Набор данных из фала
 * @param uniqueNumbers Список уникальных номеров телефонов
 */
public record CdrDto(List<CallDataRecord> callDataRecords, Set<String> uniqueNumbers) { }
