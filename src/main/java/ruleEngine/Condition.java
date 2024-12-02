package ruleEngine;

import java.lang.reflect.Field;
import java.util.Collection;

public class Condition<P extends V,V extends Comparable> {

    private String parameterName;
    private P parameter;
    private CompareType compareType;
    private V value;

    public Condition(Object globalParameter, String fieldName, CompareType compareType, V value) throws NoSuchFieldException, IllegalAccessException {
        this.parameterName = fieldName;
        this.compareType = compareType;
        this.value = value;
        Field field = globalParameter.getClass().getDeclaredField(parameterName);
        field.setAccessible(true);
        parameter = (P) field.get(globalParameter);
    }

    public boolean apply() {
        boolean result = false;
        switch (compareType){

            case EQUALS:
                return parameter.equals(value);
            case NOT_EQUALS:
                return !parameter.equals(value);
            case LESS_EXCL:
                return parameter.compareTo(value) < 0;
            case LESS_INCL:
                return parameter.compareTo(value) <= 0;
            case MORE_EXCL:
                return parameter.compareTo(value) > 0;
            case MORE_INCL:
                return parameter.compareTo(value) >= 0;
            case IN:
                return contains();
            case NOT_IN:
                return !contains();
        }
        return result;
    }

    private boolean contains() {
        if (parameter.equals(value)) {
            return true;
        } else {
            if (value instanceof Collection) {
                for (Object o : (Collection<?>) value) {
                    P current = (P) o;
                    if (parameter.equals(current)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
