package ru.gpb.grules.ruleEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TableRule extends BaseRule{

    private static final Logger LOG = LogManager.getLogger();
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
    public String perform(Object... parameters) {
        for ( Condition<?> condition : preConditions) {
            try {
                if (!condition.selectAndApply(parameters)) {
                    return null;
                }
            } catch (Exception e) {
                LOG.error(String.format("Applying error in condition %s", condition));
                LOG.error(e);
            }
        }
        StringBuilder sb = new StringBuilder();
        for ( LineRule lineRule : lineRules) {
            String result = lineRule.perform(parameters);
            if (result != null) {
                sb.append(result);
            }
        }
        return sb.toString();
    }
}
