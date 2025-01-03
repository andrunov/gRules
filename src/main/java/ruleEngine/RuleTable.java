package ruleEngine;

import java.util.List;

public class RuleTable implements Performable{

    private List<Condition<?>> preConditions;
    private List<LineRule> lineRules;



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
            if (!condition.selectAndApply(parameters)) {
                return;
            }
        }
        for ( LineRule lineRule : lineRules) {
            lineRule.perform(parameters);
        }
    }
}
