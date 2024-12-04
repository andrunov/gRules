package ruleEngine;

import exception.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Rule {

    private final String title;
    private final List<Condition<?>> conditions;
    private Action<?> action;

    public Rule(String title) {
        this.title = title;
        this.conditions = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public List<Condition<?>> getConditions() {
        return conditions;
    }

    public Action<?> getAction() {
        return action;
    }

    public void setAction(Action<?> action) {
        this.action = action;
    }

    public void perform(Object globalParameter) throws InvocationTargetException, IllegalAccessException, ParseException, NoSuchFieldException, NoSuchMethodException {
        for ( Condition<?> condition : getConditions()) {
            if (!condition.apply(globalParameter)){
                return;
            }
        }
        System.out.println(this);
        action.apply(globalParameter);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(": [");
        for (Condition<?> condition : conditions ) {
            sb.append(condition.toString());
        }
        sb.append("] ");
        sb.append(action);

        return sb.toString();
    }
}
