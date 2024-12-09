package ruleEngine;

import java.lang.reflect.InvocationTargetException;

public interface Applyable {

    boolean apply(Object parameter);
    Object extract(Object parameter) throws NoSuchFieldException, IllegalAccessException;

}
