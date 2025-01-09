package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableRuleParser {
    private static final String HEAD = "#head";
    private static final String PRIORITY = "#priority";
    private static final String CONDITION= "#condition";
    private static final String ACTION = "#action";
    private static final String PATH = "#path";
    private static final String RULE_ROW = "#row";
    private static final String END = "#end";
    private int headRow;
    private int priorityRow;
    private List<Integer> preconditionRows = new ArrayList<>();
    private List<Integer> pathList = new ArrayList<>();
    private List<Integer> ruleList = new ArrayList<>();
    private List<Integer> conditionColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();
    private Map<Integer, FieldDescriptor> conditionMap = new HashMap<>();
    private Map<Integer, FieldDescriptor> actionMap = new HashMap<>();
    private String ruleName;
    private final File parentFile;
    private final Sheet sheet;
    private final List<CellRange<?>> ranges;
    private final int firstRow;
    private int lastRow;


    public TableRuleParser(File parentFile, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        this.parentFile = parentFile;
        this.sheet = sheet;
        this.ranges = ranges;
        this.firstRow = startRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public BaseRule readRule() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        readFirstColumn();
        ruleName = getRuleName();
        TableRule result = new TableRule(getPriority());
        result.setPreConditions(readPreconditions());
        readHeader();
        readPaths();
       // extractParameters();
        result.setRules(readRules());
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
                    value = Utils.findInRanges(cell, ranges);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(HEAD)) {
                        headRow = row.getRowNum();
                    } else if (value.equals(PRIORITY)) {
                        priorityRow = row.getRowNum();
                    } else if (value.equals(CONDITION)) {
                        preconditionRows.add(row.getRowNum());
                    }  else if (value.equals(PATH)) {
                        pathList.add(row.getRowNum());
                    } else if (value.equals(RULE_ROW)) {
                        ruleList.add(row.getRowNum());
                    } else if (value.equals(END)) {
                        lastRow = row.getRowNum();
                        break;
                    }
                }
            }
        }
        if (lastRow == 0) {
            throw new ParseException(String.format("Not found end of rule File: %s Sheet: %s Row: %s", parentFile, sheet.getSheetName(), firstRow));
        }
    }

    private <V extends Comparable<V>> List<Condition<?>> readPreconditions() throws NoSuchFieldException, ClassNotFoundException {
        List<Condition<?>> result = new ArrayList<>();
        for (int rowNumber : preconditionRows) {
            Cell cell = sheet.getRow(rowNumber).getCell(1);
            Object expression = Utils.getValue(cell);
            if (expression == null) {
                expression = Utils.findInRanges(cell, ranges);
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

    private void readHeader() {
        for (Cell cell : sheet.getRow(headRow)) {
            Object value = Utils.getValue(cell);
            if (value== null) {
                value = Utils.findInRanges(cell, ranges);
            }
            if (value!=null && value.getClass().equals(String.class)) {
                if (value.equals(CONDITION)) {
                   conditionColumns.add(cell.getColumnIndex());
                } else if (value.equals(ACTION)) {
                   actionColumns.add(cell.getColumnIndex());
                }
            }
        }
    }

    private void readPaths() throws NoSuchFieldException, ClassNotFoundException {
        Map<Integer, String> paths = new HashMap<>();
        for (Integer row : pathList) {
            for (Cell cell : sheet.getRow(row)) {
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = Utils.findInRanges(cell, ranges);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    String strValue = (String) value;
                    strValue = Utils.removeRowSplitters(strValue);
                    if (!paths.containsKey(columnIndex)) {
                        paths.put(columnIndex, strValue);
                    } else {
                        String newValue = String.format("%s%s", paths.get(columnIndex), strValue);
                        paths.put(columnIndex, newValue);
                    }
                }
            }
        }
        for (Map.Entry<Integer, String> entry : paths.entrySet()) {
            Integer columnIndex = entry.getKey();
            if (conditionColumns.contains(columnIndex)) {
                conditionMap.put(columnIndex, new FieldDescriptor(entry.getValue()));
            } else if (actionColumns.contains(columnIndex)) {
                actionMap.put(columnIndex, new FieldDescriptor(entry.getValue()));
            }
        }
    }

    private <V extends Comparable<V>> List<LineRule> readRules() {
        List<LineRule> result = new ArrayList<>();
        for (Integer ruleRow : ruleList) {
            LineRule lineRule = new LineRule(0, parentFile, sheet, ruleName,"Строка " + (ruleRow + 1)); //enumeration in sheet starts from 0 and in excell is shown from 1
            result.add(lineRule);

            for (Cell cell : sheet.getRow(ruleRow)) {
                V value = (V) Utils.getValue(cell);
                if (value == null) {
                    value = (V) Utils.findInRanges(cell, ranges);
                }

                if (conditionColumns.contains(cell.getColumnIndex())) {
                    Condition<V> condition = new Condition<>(conditionMap.get(cell.getColumnIndex()));
                    lineRule.getConditions().add(condition);
                    CompareType compareType = CompareType.EQUALS;
                    if (value != null && value.getClass().equals(String.class)) {
                        String strValue = (String) value;
                        compareType = extractFrom(strValue);
                        condition.setCompareType(compareType);
                        strValue = cutOffCompareType(strValue, compareType);
                        Object enumValue = Utils.parseEnum(strValue, condition.getField());
                        if (enumValue != null) {
                            condition.setValue((V) enumValue);
                        } else {
                            condition.setValue((V) Utils.castTo(strValue));
                        }
                    } else {
                        condition.setCompareType(compareType);
                        condition.setValue(value);
                    }
                } else if (actionColumns.contains(cell.getColumnIndex())) {
                    Action<V> action = new Action<>(actionMap.get(cell.getColumnIndex()));
                    lineRule.getActions().add(action);
                    if (value instanceof String) {
                        String strValue = (String) value;
                        action.setValue((V) Utils.castTo(strValue));
                    } else if (value instanceof Double) {
                        Double dounleValue = (Double) value;
                        dounleValue = Math.ceil(dounleValue * 100) / 100;
                        action.setValue((V) dounleValue);
                    } else {
                        action.setValue(value);
                    }
                }
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
