public enum CallType {
    OUTBOX("01"), INBOX("02");

    final String code;

    CallType(String code) {
        this.code = code;
    }

    public static CallType fromCode(String code) {
        for (CallType callType : CallType.values()) {
            if (callType.code.equals(code)) {
                return callType;
            }
        }
        throw new IllegalArgumentException("Invalid call type code: " + code);
    }
}
