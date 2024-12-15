package ruleEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Action<V> extends LogicAtom {

    private Method method;
    private Field field;
    private V value;

    public Action() {
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
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

            V parameter = (V) extract(globalParameter, 1);

            if (field != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(parameter, value);
            } else if (method != null) {
                method.invoke(parameter, value);
            }
            result = true;
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("Установлено ").append(field.getName());
        sb.append("=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
