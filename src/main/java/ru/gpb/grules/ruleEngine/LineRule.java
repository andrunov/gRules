package ru.gpb.grules.ruleEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LineRule extends BaseRule{

    private static final Logger LOG = LogManager.getLogger();
    private final File parentFile;
    private final Sheet sheet;
    private final String name;
    private final String title;
    private List<Condition<?>> conditions;
    private List<Action<?>> actions;

    public LineRule(int priority, File parentFile, Sheet sheet, String name, String title) {
        super(priority);
        this.parentFile = parentFile;
        this.sheet = sheet;
        this.name = name;
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

    public void setConditions(List<Condition<?>> conditions) {
        this.conditions = conditions;
    }

    public void setActions(List<Action<?>> actions) {
        this.actions = actions;
    }

    @Override
    public void perform(Object ... parameters) {
        for ( Condition<?> condition : conditions) {
            try {
                if (!condition.selectAndApply(parameters)) {
                    return;
                }
            } catch (Exception e) {
                LOG.error(String.format("Applying error in condition %s", condition));
                LOG.error(e);
            }
        }
        for ( Action<?> action :actions) {
            try {
                action.selectAndApply(parameters);
            } catch (Exception e) {
                LOG.error(String.format("Applying error in action %s", action));
                LOG.error(e);
            }
        }
        LOG.info(this.info());
    }

    public String info() {
        StringBuilder sb = new StringBuilder();
        sb.append("Выполнено правило: ");
        sb.append("\n Файл: ").append(parentFile.getName());
        sb.append("; Лист: ").append(sheet.getSheetName());
        sb.append("; Правило: ").append(name);
        sb.append("; ").append(title);
        sb.append("\n Условия: ");
        for (Condition<?> condition : conditions ) {
            sb.append(" ").append(condition.toString()).append(";");
        }
        sb.append("\n Действия: ");
        for (Action<?> action : actions ) {
            sb.append(" ").append(action.toString()).append(";");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Правило : ");
        sb.append(parentFile.getName());
        sb.append(" : ");
        sb.append(sheet.getSheetName());
        sb.append(" : ");
        sb.append(name);
        sb.append("\n");
        sb.append(title);
        sb.append("\n");
        sb.append(" Условия: \n");
        for (Condition<?> condition : conditions ) {
            sb.append("   ").append(condition.toString());
            sb.append("\n");
        }
        sb.append(" Действия: \n");
        for (Action<?> action : actions ) {
            sb.append("   ").append(action.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
