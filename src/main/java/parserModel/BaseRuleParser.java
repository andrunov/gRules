package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.CompareType;
import ruleEngine.Condition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRuleParser extends BaseSheetParser {

    protected int priorityRow;
    protected String ruleName;
    protected final int firstRow;
    protected int lastRow;
    protected List<Integer> conditionRows = new ArrayList<>();

    public BaseRuleParser(File file, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        super(file, sheet);
        super.ranges = ranges;
        this.firstRow = startRow;
    }

    protected int getPriority() {
        Double result = null;
        if (priorityRow > firstRow) {
            Row row = sheet.getRow(priorityRow);
            Object value = Utils.getValue(row.getCell(1));
            result = (Double) value;
            if (result == null) {
                result = 0.0;
            }
        } else {
            result = 0.0;
        }
        return result.intValue();
    }

    protected String getRuleName() {
        Row row = sheet.getRow(firstRow);
        return (String) Utils.getValue(row.getCell(1));
    }

    protected  <V extends Comparable<V>> List<Condition<?>> readConditions() throws NoSuchFieldException, ClassNotFoundException {
        List<Condition<?>> result = new ArrayList<>();
        for (int rowNumber : conditionRows) {
            Cell cell = sheet.getRow(rowNumber).getCell(1);
            Object expression = Utils.getValue(cell);
            if (expression == null) {
                expression = findInRanges(cell);
            }
            if (expression != null && expression.getClass().equals(String.class)) {
                int spase = ((String) expression).indexOf(' ');
                String path = ((String) expression).substring(0, spase);
                String value = ((String) expression).substring(spase + 1);
                FieldDescriptor fieldDescriptor = new FieldDescriptor(path);
                Condition<V> condition = new Condition<>(fieldDescriptor);
                CompareType compareType = extractFrom(value);
                condition.setCompareType(compareType);
                value = cutOffCompareType(value, compareType);
                Object enumValue = Utils.parseEnum(value, condition.getField());
                if (enumValue != null) {
                    condition.setValue((V) enumValue);
                } else {
                    condition.setValue((V) Utils.castTo(value));
                }
                result.add(condition);
            }
        }
        return result;
    }


    protected CompareType extractFrom(String value) {
        for (CompareType compareType : CompareType.values()) {
            if (value.startsWith(compareType.getValue())) {
                return compareType;
            }
        }
        return CompareType.EQUALS;
    }

    protected String cutOffCompareType(String value, CompareType compareType) {
        if (value.startsWith(compareType.getValue())) {
            return value.substring(compareType.getValue().length() + 1);
        }
        //default if compare type not transferred
        return value;
    }

    protected void initFields() throws ParseException {
        readFirstColumn();
        ruleName = getRuleName();
    }

    protected abstract void readFirstColumn() throws ParseException;
}
