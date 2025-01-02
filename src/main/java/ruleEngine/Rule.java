package ruleEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rule implements Performable{

    private final File parentFile;
    private final String title;
    private final List<Condition<?>> conditions;
    private final List<Action<?>> actions;

    public Rule(File parentFile, String title) {
        this.parentFile = parentFile;
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

    @Override
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
        sb.append(parentFile.getPath());
        sb.append("\n");
        sb.append(title);
        sb.append(": [ ");
        for (Condition<?> condition : conditions ) {
            sb.append(condition.toString());
        }
        sb.append("] { ");
        for (Action<?> action : actions ) {
            sb.append(action.toString());
        }
        sb.append(" }");

        return sb.toString();
    }
}
