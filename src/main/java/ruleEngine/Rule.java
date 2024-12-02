package ruleEngine;

import java.util.List;

public class Rule {
    private String title;
    private List<Condition<?,?>> conditions;
    private Action<?,?> action;

}
