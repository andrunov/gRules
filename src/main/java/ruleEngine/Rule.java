package ruleEngine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Rule {

    private final String title;
    private final List<Condition<?,?>> conditions;
    private Action<?,?> action;

    public Rule(String title) {
        this.title = title;
        this.conditions = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public List<Condition<?, ?>> getConditions() {
        return conditions;
    }

    public Action<?, ?> getAction() {
        return action;
    }

    public void setAction(Action<?, ?> action) {
        this.action = action;
    }

    public void perform() throws InvocationTargetException, IllegalAccessException {
        for ( Condition<?, ?> condition : getConditions()) {
            if (!condition.apply()){
                return;
            }
        }
        action.apply();
    }
}
