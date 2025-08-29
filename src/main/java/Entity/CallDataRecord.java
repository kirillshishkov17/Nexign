package Entity;

import java.time.LocalDateTime;
import java.util.Optional;

public class CallDataRecord {
    private CallType callType;
    private String phoneNumber;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private TariffType tariffType;

    private Optional<Double> price = Optional.empty();
    private Optional<Double> duration = Optional.empty();

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

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public TariffType getTariffType() {
        return tariffType;
    }

    public void setTariffType(TariffType tariffType) {
        this.tariffType = tariffType;
    }

    public Optional<Double> getPrice() {
        return price;
    }

    public void setPrice(Optional<Double> price) {
        this.price = price;
    }

    public Optional<Double> getDuration() {
        return duration;
    }

    public void setDuration(Optional<Double> duration) {
        this.duration = duration;
    }
}
