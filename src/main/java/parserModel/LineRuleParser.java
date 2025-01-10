package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LineRuleParser extends BaseRuleParser {

    private List<Integer> actionRows = new ArrayList<>();

    public LineRuleParser(File file, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        super(file, sheet, ranges,startRow);
    }

    public int getLastRow() {
        return lastRow;
    }

    public BaseRule readRule() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        readFirstColumn();
        ruleName = getRuleName();
        LineRule result = new LineRule(getPriority(), file, sheet, ruleName, "Строка " + (firstRow + 1));
        result.setConditions(readConditions());
        result.setActions(readActions());
        return result;
    }

    private int getPriority() {
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

    private String getRuleName() {
        Row row = sheet.getRow(firstRow);
        return (String) Utils.getValue(row.getCell(1));
    }

    private void readFirstColumn() throws ParseException {
        for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = super.findInRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(PRIORITY)) {
                        priorityRow = row.getRowNum();
                    } else if (value.equals(CONDITION)) {
                        conditionRows.add(row.getRowNum());
                    } else if (value.equals(ACTION)) {
                        actionRows.add(row.getRowNum());
                    } else if (value.equals(END)) {
                        lastRow = row.getRowNum();
                        break;
                    }
                }
            }
        }
        if (lastRow == 0) {
            throw new ParseException(String.format("Not found end of rule File: %s Sheet: %s Row: %s", file, sheet.getSheetName(), firstRow));
        }
    }

    private <V extends Comparable<V>> List<Condition<?>> readConditions() throws NoSuchFieldException, ClassNotFoundException {
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

    private <V extends Comparable<V>> List<Action<?>> readActions() throws NoSuchFieldException, ClassNotFoundException {
        List<Action<?>> result = new ArrayList<>();
        for (int rowNumber : actionRows) {
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
                Action<V> action = new Action<>(fieldDescriptor);
                value = cutOffCompareType(value, CompareType.EQUALS);
                Object enumValue = Utils.parseEnum(value, action.getField());
                if (enumValue != null) {
                    action.setValue((V) enumValue);
                } else {
                    action.setValue((V) Utils.castTo(value));
                }
                result.add(action);
            }
        }
        return result;
    }


    private CompareType extractFrom(String value) {
        for (CompareType compareType : CompareType.values()) {
            if (value.startsWith(compareType.getValue())) {
                return compareType;
            }
        }
        return CompareType.EQUALS;
    }

    private String cutOffCompareType(String value, CompareType compareType) {
        if (value.startsWith(compareType.getValue())) {
             return value.substring(compareType.getValue().length() + 1);
        }
        //default if compare type not transferred
        return value;
    }


}
