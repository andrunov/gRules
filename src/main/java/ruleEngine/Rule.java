package ruleEngine;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    private final String title;
    private final List<Condition<?>> conditions;
    private final List<Action<?>> actions;

    public Rule(String title) {
        this.title = title;
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public List<Condition<?>> getConditions() {
        return conditions;
    }

    public List<Action<?>> getActions() {
        return actions;
    }

    public void perform(Object ... parameters) {
        for ( Condition<?> condition : conditions) {
            if (!condition.selectAndApply(parameters)) {
                return;
            }
        }
        for ( Action<?> action :actions) {
            action.selectAndApply(parameters);
        }
        System.out.println(this);
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
        for (Action<?> action : actions ) {
            sb.append(action.toString());
        }
        sb.append("] ");

        return sb.toString();
    }
}
