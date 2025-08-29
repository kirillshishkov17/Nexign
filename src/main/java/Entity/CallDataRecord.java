package Entity;

import java.time.LocalDateTime;

public record CallDataRecord(
        CallType callType,
        String phoneNumber,
        LocalDateTime startTime,
        LocalDateTime stopTime,
        TariffType tariffType
) { }
