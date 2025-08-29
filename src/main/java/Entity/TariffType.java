package Entity;

public enum TariffType {
    UNLIMITED("06"), PER_MINUTE("03"), COMMON("11");

    public final String code;

    TariffType(String code) {
        this.code = code;
    }

    public static TariffType fromCode(String code) {
        for (TariffType tariffType : TariffType.values()) {
            if (tariffType.code.equals(code)) {
                return tariffType;
            }
        }
        throw new IllegalArgumentException("Invalid tariff type code: " + code);
    }
}
