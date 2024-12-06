package ruleEngine;

import java.lang.reflect.Field;
import java.util.Collection;

public class Condition<V extends Comparable> {

    private Field field;
    private V parameter;
    private CompareType compareType;
    private V value;



    public Condition() {

    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public V getParameter() {
        return parameter;
    }

    public void setParameter(V parameter) {
        this.parameter = parameter;
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public boolean apply(Object globalParameter) {

        boolean result = false;

        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            parameter = (V) field.get(globalParameter);
        } catch (Exception e) {
            return result;
        }

        switch (compareType){

            case EQUALS:
                result = parameter.equals(value);
                return result;
            case NOT_EQUALS:
                result = !parameter.equals(value);
                return result;
            case LESS_EXCL:
                result = parameter.compareTo(value) < 0;
                return result;
            case LESS_INCL:
                result = parameter.compareTo(value) <= 0;
                return result;
            case MORE_EXCL:
                result = parameter.compareTo(value) > 0;
                return result;
            case MORE_INCL:
                result = parameter.compareTo(value) >= 0;
                return result;
            case IN:
                result = contains();
                return result;
            case NOT_IN:
                result = !contains();
                return result;
        }
        return result;
    }

    private boolean contains() {
        if (parameter.equals(value)) {
            return true;
        } else {
            if (value instanceof Collection) {
                for (Object o : (Collection<?>) value) {
                    V current = (V) o;
                    if (parameter.equals(current)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return field.getName() + compareType.getValue() + value + "; ";
    }
}
