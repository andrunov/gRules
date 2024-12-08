package ruleEngine;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Condition<V extends Comparable> {

    private Field field;
    private V parameter;
    private List<String> parameterPath;
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

    public List<String> getParameterPath() {
        return parameterPath;
    }

    public void setParameterPath(List<String> parameterPath) {
        this.parameterPath = parameterPath;
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
            parameter = (V) extractParameter(globalParameter, 0);
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

    private Object extractParameter(Object globalParameter, int index) throws NoSuchFieldException, IllegalAccessException {
         Field field = globalParameter.getClass().getDeclaredField(parameterPath.get(index));
         if (!field.isAccessible()) {
             field.setAccessible(true);
         }
         Object obj = field.get(globalParameter);
         if (parameterPath.size() == index + 1) {
             return obj;
         } else {
             return extractParameter(obj, index + 1);
         }
    }

    private Field extractField(Field field, int index) throws NoSuchFieldException {
        Field resultField = field.getType().getDeclaredField(parameterPath.get(index));
        if (parameterPath.size() == index + 1) {
            return resultField;
        } else {
            return extractField(resultField, index + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String par : parameterPath) {
            counter ++;
            if (parameterPath.size() > counter) {
                sb.append(String.format("%s.", par));
            } else {
                sb.append(String.format("%s ", par));
            }
        }
        sb.append(String.format("%s %s; ", compareType.getValue(), value));
        return sb.toString();
    }
}
