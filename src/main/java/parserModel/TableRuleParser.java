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

/* reads TableRule class from excel
 */
public class TableRuleParser extends BaseRuleParser {

    private int headRow;
    private List<Integer> pathList = new ArrayList<>();
    private List<Integer> ruleList = new ArrayList<>();
    private List<Integer> conditionColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();
    private Map<Integer, FieldDescriptor> conditionMap = new HashMap<>();
    private Map<Integer, FieldDescriptor> actionMap = new HashMap<>();

    public TableRuleParser(File file, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        super(file, sheet, ranges,startRow);
    }


    public int getLastRow() {
        return lastRow;
    }

    public BaseRule readRule() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        initFields();
        TableRule result = new TableRule(getPriority());
        result.setPreConditions(readConditions());
        readHeader();
        readPaths();
        result.setRules(readRules());
        return result;
    }

    protected void readFirstColumn() throws ParseException {
        for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = getValue(cell);
                if (value == null) {
                    value = findInRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(HEAD)) {
                        headRow = row.getRowNum();
                    } else if (value.equals(PRIORITY)) {
                        priorityRow = row.getRowNum();
                    } else if (value.equals(CONDITION)) {
                        conditionRows.add(row.getRowNum());
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
            throw new ParseException(String.format("Not found end of rule File: %s Sheet: %s Row: %s", file, sheet.getSheetName(), firstRow));
        }
    }



    private void readHeader() {
        for (Cell cell : sheet.getRow(headRow)) {
            Object value = getValue(cell);
            if (value== null) {
                value = findInRanges(cell);
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
                Object value = getValue(cell);
                if (value == null) {
                    value = findInRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    String strValue = (String) value;
                    strValue = removeRowSplitters(strValue);
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
            LineRule lineRule = new LineRule(0, file, sheet, ruleName,"Строка " + (ruleRow + 1)); //enumeration in sheet starts from 0 and in excell is shown from 1
            result.add(lineRule);

            for (Cell cell : sheet.getRow(ruleRow)) {
                V value = (V) getValue(cell);
                if (value == null) {
                    value = (V) findInRanges(cell);
                }

                if (conditionColumns.contains(cell.getColumnIndex())) {
                    Condition<V> condition = new Condition<>(conditionMap.get(cell.getColumnIndex()));
                    if (value != null) {
                        lineRule.getConditions().add(condition);
                        CompareType compareType = CompareType.EQUALS;
                        if (value.getClass().equals(String.class)) {
                            String strValue = (String) value;
                            compareType = extractFrom(strValue);
                            condition.setCompareType(compareType);
                            strValue = cutOffCompareType(strValue, compareType);
                            condition.setValue((V) parseFrom(strValue, condition.getField().getType()));
                        } else {
                            condition.setCompareType(compareType);
                            condition.setValue(value);
                        }
                    }
                } else if (actionColumns.contains(cell.getColumnIndex())) {
                    Action<V> action = new Action<>(actionMap.get(cell.getColumnIndex()));
                    lineRule.getActions().add(action);
                    if (value != null) {
                        if (value.getClass().equals(String.class)) {
                            String strValue = (String) value;
                            action.setValue((V) parseFrom(strValue, action.getField().getType()));
                        } else if (value.getClass().equals(Double.class)) {
                            Double doubleValue = (Double) value;
                            doubleValue = Math.ceil(doubleValue * 100) / 100;
                            action.setValue((V) doubleValue);
                        } else {
                            action.setValue(value);
                        }
                    }
                }
            }
        }
        return result;
    }



}
