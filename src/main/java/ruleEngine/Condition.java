package ruleEngine;

import parserModel.FieldDescriptor;

import java.lang.reflect.Field;
import java.util.Collection;

public class Condition<V extends Comparable<V>> extends LogicAtom {

    private Field field;
    private V parameter;
    private CompareType compareType;
    private V value;

    public Condition (FieldDescriptor fieldDescriptor) {
        field = fieldDescriptor.getField();
        super.parameterPath = fieldDescriptor.getParameterPath();
        super.parameterType = fieldDescriptor.getType();
    }

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

    @Override
    public boolean apply(Object globalParameter) {

        boolean result = false;

        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            this.parameter = (V) extract(globalParameter, 0);
        } catch (Exception e) {
            return result;
        }

        switch (compareType){

            case EQUALS:
                result = this.parameter.equals(value);
                return result;
            case NOT_EQUALS:
                result = !this.parameter.equals(value);
                return result;
            case LESS_EXCL:
                result = this.parameter.compareTo(value) < 0;
                return result;
            case LESS_INCL:
                result = this.parameter.compareTo(value) <= 0;
                return result;
            case MORE_EXCL:
                result = this.parameter.compareTo(value) > 0;
                return result;
            case MORE_INCL:
                result = this.parameter.compareTo(value) >= 0;
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
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String par : parameterPath) {
            counter ++;
            if (super.getParameterPath().size() > counter) {
                sb.append(String.format("%s.", par));
            } else {
                sb.append(String.format("%s ", par));
            }
        }
        sb.append(String.format("%s %s; ", compareType.getValue(), value));
        return sb.toString();
    }
}
