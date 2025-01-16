package ru.gpb.grules.ruleEngine;

import ru.gpb.grules.parserModel.FieldDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action<V> extends LogicAtom<V> {

    private Method method;

    public Action(FieldDescriptor fieldDescriptor) {
        super(fieldDescriptor);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean apply(Object globalParameter) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {

        boolean result = false;
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
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getSimpleName(((Class<?>) parameterType).getName())).append(".");
        sb.append(field.getName());
        sb.append("=").append(value).append("; ");
        return sb.toString();
    }
}
