package ruleEngine;

public interface Applyable {

    Object select(Object ... parameters);
    boolean apply(Object parameter);
    Object extract(Object parameter, int startIndex, int depth) throws NoSuchFieldException, IllegalAccessException;

}
