package ruleEngine;

public enum CompareType {
    EQUALS("="),
    NOT_EQUALS("!="),
    IN("IN"),
    NOT_IN("!IN"),
    MORE_INCL(">="),
    MORE_EXCL(">"),
    LESS_INCL("<="),
    LESS_EXCL("<");

    private final String value;

    CompareType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CompareType fromString(String text) {
        for (CompareType a : CompareType.values()) {
            if (a.value.equalsIgnoreCase(text)) {
                return a;
            }
        }
        return null;
    }

}
