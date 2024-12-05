package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.Action;
import ruleEngine.CompareType;
import ruleEngine.Condition;
import ruleEngine.Rule;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelParser {

    private static final String CONDITION= "#condition";
    private static final String ACTION = "#action";
    private static final String HEAD = "#head";
    private static final String KEY = "#key";
    private static final String VALUE = "#val";

    private int headRow;
    private List<Integer> keyList = new ArrayList<>();
    private List<Integer> valueList  = new ArrayList<>();

    //TODO remove
    private List<Integer> conditionColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();

    Map<Integer, String> conditionMap = new HashMap<>();
    Map<Integer, String> actionMap = new HashMap<>();
    private Map<Integer, Rule> ruleMap  = new HashMap<>();

    Sheet sheet;
    List<CellRange<?>> ranges;

    int leftBorder = 0;
    int rightBorder = 0;
    int upBorder = 0;
    int downBorder = 0;

    public Map<Integer, Rule> getRuleMap() {
        return ruleMap;
    }

    public void readSheet(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheetAt(0);
        initRanges();
        readFirstColumn();
        readHeader();
        readKeys();
        readRules();

    }

    private void initRanges() throws ClassNotFoundException {
        ranges = new ArrayList<>();
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
        }
    }

    private void readFirstColumn() {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            Object value = Utils.getValue(cell);
            if (value== null) {
                value = lookUpАorRanges(cell);
            }
            if (value!=null && value.getClass().equals(String.class)) {
                if (value.equals(HEAD)) {
                    headRow = row.getRowNum();
                } else if (value.equals(KEY)) {
                    keyList.add(row.getRowNum());
                } else if (value.equals(VALUE)) {
                    valueList.add(row.getRowNum());
                }
            }
        }
    }

    private void readHeader() {
        for (Cell cell : sheet.getRow(headRow)) {
            Object value = Utils.getValue(cell);
            if (value== null) {
                value = lookUpАorRanges(cell);
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

    private void readKeys() {
        for (Integer keyRow : keyList) {
            for (Cell cell : sheet.getRow(keyRow)) {
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    if (conditionColumns.contains(columnIndex)) {
                        if (conditionMap.get(columnIndex) == null) {
                            conditionMap.put(columnIndex, (String) value);
                        } else {
                            String oldValue = conditionMap.get(columnIndex);
                            String newValue = String.format("%s.%s", oldValue, value);
                            conditionMap.put(columnIndex, newValue);
                        }
                    } else if (actionColumns.contains(columnIndex)) {
                        if (actionMap.get(columnIndex) == null) {
                            actionMap.put(columnIndex, (String) value);
                        } else {
                            String oldValue = actionMap.get(columnIndex);
                            String newValue = String.format("%s.%s", oldValue, value);
                            actionMap.put(columnIndex, newValue);
                        }
                    }
                }
            }
        }
    }

    private <V extends Comparable> void readRules() throws ParseException, NoSuchFieldException, NoSuchMethodException {
        for (Integer ruleRow : valueList) {
            for (Cell cell : sheet.getRow(ruleRow)) {
                Rule rule = new Rule("Строка " + ruleRow);
                ruleMap.put(ruleRow, rule);

                V value = (V) Utils.getValue(cell);
                if (value == null) {
                    value = (V) lookUpАorRanges(cell);
                }

                if (conditionColumns.contains(cell.getColumnIndex())) {
                    Condition<V> condition = new Condition<>();
                    rule.getConditions().add(condition);
                    condition.setParameterName(conditionMap.get(cell.getColumnIndex()));
                    CompareType compareType = CompareType.EQUALS;
                    if (value != null && value.getClass().equals(String.class)) {
                        String strValue = (String) value;
                        compareType = extractFrom(strValue);
                        condition.setCompareType(compareType);
                        strValue = cutOffCompareType(strValue, compareType);
                        value = (V) Utils.castTo(strValue);
                        condition.setValue(value);
                    } else {
                        condition.setCompareType(compareType);
                        condition.setValue(value);
                    }
                } else if (actionColumns.contains(cell.getColumnIndex())) {
                    Action<V> action = new Action<>();
                    action.setMethodOrField(conditionMap.get(cell.getColumnIndex()));
                    rule.setAction(action);
                    if (value instanceof String) {
                        String strValue = (String) value;
                        value = (V) Utils.castTo(strValue);
                        action.setValue(value);
                    } else {
                        action.setValue(value);
                    }
                }
            }
        }
    }



    public  <V extends Comparable> void readConditions(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ruleMap = new HashMap<>();
        initRanges();

        leftBorder = 0;
        rightBorder = 0;
        upBorder = -1;
        downBorder = Integer.MAX_VALUE;

        for (Row row : sheet) {

            Rule rule = null;
            if (acceptRow(row)) {
                rule = new Rule("Строка " + row.getRowNum());
                ruleMap.put(row.getRowNum(), rule);
            }

            int columnCounter = 0;

            for (Cell cell : row) {

                V value = (V) lookUpАorRanges(cell);
                if (value == null) {
                    value = (V) Utils.getValue(cell);
                }
                columnCounter++;

                if (value.getClass().equals(String.class)) {

                }



                if (acceptKey(cell)) {
                    conditionMap.put(columnCounter, (String) value);
                } else if (acceptValue(cell)) {

                    if (columnCounter < conditionMap.size()) {
                        //conditions
                        //String strCondition = String.format("%-15.15s   %-15.15s", keyMap.get(columnCounter), value);
                        //conditionMap.get(row.getRowNum()).add(strCondition);
                        Condition<V> condition = new Condition<>();
                        rule.getConditions().add(condition);
                        condition.setParameterName(conditionMap.get(columnCounter));
                        CompareType compareType = CompareType.EQUALS;
                        if (value instanceof String) {
                            String strValue = (String) value;
                            compareType = extractFrom(strValue);
                            condition.setCompareType(compareType);
                            strValue = cutOffCompareType(strValue, compareType);
                            value = (V) Utils.castTo(strValue);
                            condition.setValue(value);
                        } else {
                            condition.setCompareType(compareType);
                            condition.setValue(value);
                        }
                    } else {
                        //actions
                        //String strCondition = String.format("%-15.15s   %-15.15s", keyMap.get(columnCounter), value);
                        //conditionMap.get(row.getRowNum()).add(strCondition);
                        Action<V> action = new Action<>();
                        action.setMethodOrField(conditionMap.get(columnCounter));
                        rule.setAction(action);
                        if (value instanceof String) {
                            String strValue = (String) value;
                            value = (V) Utils.castTo(strValue);
                            action.setValue(value);
                        } else {
                            action.setValue(value);
                        }
                    }
                }

            }
            ruleMap.remove(downBorder);

        }
    }

    public Object lookUpАorRanges(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.BLANK) {
            for (CellRange<?> range : ranges) {
                if (range.contains(cell)) {
                    return range.getValue();
                }
            }
        }
        return null;
    }


    private boolean acceptRow(Row row) {
        return upBorder != -1 && row.getRowNum() >= upBorder + 1;
    }

    private boolean acceptKey(Cell cell) {
        return  cell.getRowIndex() == upBorder
            && cell.getColumnIndex() >= leftBorder
            && cell.getColumnIndex() <= rightBorder;
    }

    private boolean acceptValue(Cell cell) {
        return  cell.getRowIndex() > upBorder
                && cell.getColumnIndex() >= leftBorder
                && cell.getColumnIndex() <= rightBorder
                && cell.getRowIndex() < downBorder;
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
