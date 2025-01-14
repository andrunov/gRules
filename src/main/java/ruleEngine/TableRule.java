package ruleEngine;

import java.util.List;

public class TableRule extends BaseRule{

    private List<Condition<?>> preConditions;
    private List<LineRule> lineRules;

    public TableRule(int priority) {
        super(priority);
    }


    public List<Condition<?>> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<Condition<?>> preConditions) {
        this.preConditions = preConditions;
    }

    public List<LineRule> getRules() {
        return lineRules;
    }

    public void setRules(List<LineRule> lineRules) {
        this.lineRules = lineRules;
    }

    @Override
    public void perform(Object... parameters) {
        for ( Condition<?> condition : preConditions) {
            try {
                if (!condition.selectAndApply(parameters)) {
                    return;
                }
            } catch (Exception e) {
                System.out.printf("%s %s%n", condition, e.getMessage());
                //TODO replase with Logger error
                // throw new RuntimeException(String.format("%s %s", condition, e.getMessage()));
            }
        }
        for ( LineRule lineRule : lineRules) {
            lineRule.perform(parameters);
        }
    }
}
