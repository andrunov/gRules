package ruleEngine;

import exception.ParseException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action<V> {

    private Object globalParameter;

    private String methodOrField;
    private Method method;

    private Field field;
    private V value;

    public Action(Object globalParameter,  String methodOrName, V value) throws NoSuchMethodException, NoSuchFieldException, ParseException {
       if (methodOrName != null) {
           if (methodOrName.startsWith(".")) {
               this.globalParameter = globalParameter;
               this.value = value;
               this.field = null;
               this.method = globalParameter.getClass().getDeclaredMethod(methodOrName.substring(1), value.getClass());

           } else {
               this.globalParameter = globalParameter;
               this.value = value;
               this.field = globalParameter.getClass().getDeclaredField(methodOrName);
               this.method = null;
           }
       } else {
           throw new ParseException("Не передано имя поля или метода ");
       }
    }

    public Action() {
    }



    public Object getGlobalParameter() {
        return globalParameter;
    }

    public void setGlobalParameter(Object globalParameter) {
        this.globalParameter = globalParameter;
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

    public void apply(Object globalParameter) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, ParseException {
        if (methodOrField != null) {
            if (methodOrField.startsWith(".")) {
                this.field = null;
                this.method = globalParameter.getClass().getDeclaredMethod(methodOrField.substring(1), value.getClass());

            } else {
                this.field = globalParameter.getClass().getDeclaredField(methodOrField);
                this.method = null;
            }
        } else {
            throw new ParseException("Не передано имя поля или метода ");
        }

        if (field != null) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(globalParameter, value);
        } else if (method != null) {
            method.invoke(globalParameter, value);
        }
    }

    public void setMethodOrField(String methodOrField) throws NoSuchMethodException, NoSuchFieldException, ParseException {
        this.methodOrField = methodOrField;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("Установлено ").append(methodOrField);
        sb.append("=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
