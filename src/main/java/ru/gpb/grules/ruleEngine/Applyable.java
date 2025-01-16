package ru.gpb.grules.ruleEngine;

import java.lang.reflect.InvocationTargetException;

public interface Applyable {

    Object select(Object ... parameters);
    boolean apply(Object parameter) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, IllegalArgumentException;
    Object extract(Object parameter, int depth) throws NoSuchFieldException, IllegalAccessException;

}
