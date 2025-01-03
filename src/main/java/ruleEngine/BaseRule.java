package ruleEngine;

public abstract class BaseRule implements Performable, Comparable<BaseRule> {
    private int priority;

    @Override
    public int compareTo(BaseRule o) {
        return this.priority - o.priority;
    }
}
