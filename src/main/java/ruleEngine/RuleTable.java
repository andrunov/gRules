package ruleEngine;

import java.util.List;

public class RuleTable implements Performable{

    private List<Condition<?>> preConditions;
    private List<Rule> rules;

    public List<Condition<?>> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<Condition<?>> preConditions) {
        this.preConditions = preConditions;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public void perform(Object... parameters) {
        for ( Condition<?> condition : preConditions) {
            if (!condition.selectAndApply(parameters)) {
                return;
            }
        }
        for ( Rule rule : rules) {
            rule.perform(parameters);
        }
    }
}
