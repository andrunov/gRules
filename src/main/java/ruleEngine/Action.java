package ruleEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action<P extends V,V> {

    Object globalParameter;
    Method method;
    private final V value;

    public Action(Object globalParameter, String methodName, V value) throws NoSuchMethodException {
        this.globalParameter = globalParameter;
        this.value = value;
        this.method = globalParameter.getClass().getDeclaredMethod(methodName, value.getClass());
    }

    public void apply() throws IllegalAccessException, InvocationTargetException {
        method.invoke(globalParameter, value);
    }




}
