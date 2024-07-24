import java.time.LocalDateTime;

public class CallDataRecord {
    private final CallType callType;
    private final String phoneNumber;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private final TariffType tariffType;

    public CallDataRecord(CallType callType, String phoneNumber, LocalDateTime startTime, LocalDateTime stopTime, TariffType tariffType) {
        this.callType = callType;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.tariffType = tariffType;
    }

    public CallType getCallType() {
        return callType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public TariffType getTariffType() {
        return tariffType;
    }

    @Override
    public String toString() {
        return "CallDataRecord{" +
                "callType=" + callType +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", tariffType=" + tariffType +
                '}';
    }
}
