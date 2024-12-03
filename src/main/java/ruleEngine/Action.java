package ruleEngine;

import exception.ParseException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action<P extends V,V> {

    Object globalParameter;
    Method method;

    Field field;
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


    public void apply() throws IllegalAccessException, InvocationTargetException {
        if (field != null) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(globalParameter, value);
        } else if (method != null) {
            method.invoke(globalParameter, value);
        }
    }




}
