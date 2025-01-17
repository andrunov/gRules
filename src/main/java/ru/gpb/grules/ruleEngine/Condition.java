package ru.gpb.grules.ruleEngine;

import ru.gpb.grules.parserModel.FieldDescriptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

public class Condition<V extends Comparable<V>> extends LogicAtom<V> {

    private V parameter;
    private CompareType compareType;

    public Condition(FieldDescriptor fieldDescriptor) {
        super(fieldDescriptor);
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

    @Override
    public boolean apply(Object globalParameter) throws NoSuchFieldException, IllegalAccessException {

        boolean result = false;
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        this.parameter = extract(globalParameter, 0);

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
        sb.append(getSimpleName(((Class<?>) parameterType).getName())).append(".");
        int counter = 0;
        for (String par : parameterPath) {
            counter ++;
            if (super.getParameterPath().size() > counter) {
                sb.append(String.format("%s.", par));
            } else {
                sb.append(String.format("%s ", par));
            }
        }
        if (value instanceof Calendar) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateValue = format.format(((Calendar)value).getTime());
            sb.append(String.format("%s %s", compareType.getValue(), dateValue));
        } else {
            sb.append(String.format("%s %s", compareType.getValue(), value));
        }
        return sb.toString();
    }
}
