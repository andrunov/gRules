package ru.gpb.grules.ruleEngine;

public abstract class BaseRule implements Performable, Comparable<BaseRule> {
    private final int priority;

    public BaseRule(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(BaseRule o) {
        return this.priority - o.priority;
    }
}
