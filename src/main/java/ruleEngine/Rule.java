package ruleEngine;

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

    public void perform(Object ... parameters) {
        for ( Condition<?> condition : getConditions()) {
            if (!condition.selectAndApply(parameters)) {
                return;
            }
        }
        if (action.selectAndApply(parameters)) {
            System.out.println(this);
        }
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
