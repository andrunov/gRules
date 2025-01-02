package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RulelParser {
    private static final String CONDITION= "#condition";
    private static final String ACTION = "#action";
    private static final String HEAD = "#head";
    private static final String DIR = "#dir";
    private static final String CLASS = "#class";
    private static final String FIELD = "#field";
    private static final String RULE_ROW = "#row";
    private static final String END = "#end";
    private List<Integer> preconditionRows = new ArrayList<>();
    private int headRow;
    private List<Integer> dirList = new ArrayList<>();
    private List<Integer> classList = new ArrayList<>();
    private List<Integer> fieldList = new ArrayList<>();
    private List<Integer> ruleList = new ArrayList<>();
    private List<Integer> conditionColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();

    private List<Applyable> preconditions = new ArrayList<>();
    private Map<Integer, FieldDescriptor> conditionMap = new HashMap<>();
    private Map<Integer, FieldDescriptor> actionMap = new HashMap<>();

    private Sheet sheet;
    private List<CellRange<?>> ranges;

    private int firstRow;
    private int lastRow;


    public RulelParser(Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        this.sheet = sheet;
        this.ranges = ranges;
        this.firstRow = startRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public Performable readSheet(File parentFile) throws ClassNotFoundException, NoSuchFieldException {
        readFirstColumn();
        RuleTable result = new RuleTable();
        result.setPreConditions(readPreconditions());
        readHeader();
        readClasses();
        readFields();
        readDirs();
        extractParameters();
        result.setRules(readRules(parentFile));
        return result;
    }

    private void readFirstColumn() {
        for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(CONDITION)) {
                        preconditionRows.add(row.getRowNum());
                    } else if (value.equals(HEAD)) {
                        headRow = row.getRowNum();
                    } else if (value.equals(DIR)) {
                        dirList.add(row.getRowNum());
                    } else if (value.equals(CLASS)) {
                        classList.add(row.getRowNum());
                    } else if (value.equals(FIELD)) {
                        fieldList.add(row.getRowNum());
                    } else if (value.equals(RULE_ROW)) {
                        ruleList.add(row.getRowNum());
                    } else if (value.equals(END)) {
                        lastRow = row.getRowNum();
                        break;
                    }
                }
            }
        }
    }

    private <V extends Comparable<V>> List<Condition<?>> readPreconditions() throws NoSuchFieldException, ClassNotFoundException {
        List<Condition<?>> result = new ArrayList<>();
        for (int rowNumber : preconditionRows) {
            Cell cell = sheet.getRow(rowNumber).getCell(1);
            Object value = Utils.getValue(cell);
            if (value == null) {
                value = lookUpАorRanges(cell);
            }
            if (value != null && value.getClass().equals(String.class)) {
                int spase = ((String) value).indexOf(' ');
                String path = ((String) value).substring(0, spase);
                String expression = ((String) value).substring(spase + 1);
                FieldDescriptor fieldDescriptor = FieldDescriptor.from(path);
                fieldDescriptor.extractFieldAndPar();
                Condition<V> condition = new Condition<>(fieldDescriptor);
                CompareType compareType = extractFrom(expression);
                condition.setCompareType(compareType);
                expression = cutOffCompareType(expression, compareType);
                Object enumValue = Utils.parseEnum(expression, condition.getField());
                if (enumValue != null) {
                    condition.setValue((V) enumValue);
                } else {
                    value = (V) Utils.castTo(expression);
                    condition.setValue((V) value);
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

    private void readDirs() {
        for (Integer row : dirList) {
            for (Cell cell : sheet.getRow(row)) {
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    if (conditionColumns.contains(columnIndex)) {
                        conditionMap.get(columnIndex).setDirName((String) value);
                    } else if (actionColumns.contains(columnIndex)) {
                        actionMap.get(columnIndex).setDirName((String) value);
                    }
                }
            }
        }
    }

    private void extractParameters() throws NoSuchFieldException, ClassNotFoundException {
        for (FieldDescriptor fieldDescriptor : conditionMap.values()) {
            fieldDescriptor.extractFieldAndPar();
        }
        for (FieldDescriptor fieldDescriptor : actionMap.values()) {
            fieldDescriptor.extractFieldAndPar();
        }
    }

    private void readClasses() {
        for (Integer row : classList) {
            for (Cell cell : sheet.getRow(row)) {
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    if (conditionColumns.contains(columnIndex)) {
                        conditionMap.put(columnIndex, new FieldDescriptor ((String) value));
                    } else if (actionColumns.contains(columnIndex)) {
                        actionMap.put(columnIndex, new FieldDescriptor ((String) value));
                    }
                }
            }
        }
    }

    private void readFields() {
        for (Integer row : fieldList) {
            for (Cell cell : sheet.getRow(row)) {
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {
                    int columnIndex = cell.getColumnIndex();
                    String strValue = Utils.removeRowSplitters((String) value);
                    if (conditionColumns.contains(columnIndex)) {
                        conditionMap.get(columnIndex).setFieldName(strValue);
                    } else if (actionColumns.contains(columnIndex)) {
                        actionMap.get(columnIndex).setFieldName(strValue);
                    }
                }
            }
        }
    }

    private <V extends Comparable<V>> List<Rule> readRules(File parentFile) throws NoSuchFieldException, ClassNotFoundException {
        List<Rule> result = new ArrayList<>();
        for (Integer ruleRow : ruleList) {
            Rule rule = new Rule(parentFile, "Строка " + (ruleRow + 1)); //enumeration in sheet starts from 0 and in excell is shown from 1
            result.add(rule);

            for (Cell cell : sheet.getRow(ruleRow)) {
                V value = (V) Utils.getValue(cell);
                if (value == null) {
                    value = (V) lookUpАorRanges(cell);
                }

                if (conditionColumns.contains(cell.getColumnIndex())) {
                    Condition<V> condition = new Condition<>();
                    rule.getConditions().add(condition);
                    condition.setField(conditionMap.get(cell.getColumnIndex()).getField());
                    condition.setParameterPath(conditionMap.get(cell.getColumnIndex()).getParameterPath());
                    condition.setParameterType(conditionMap.get(cell.getColumnIndex()).getType());
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
                            value = (V) Utils.castTo(strValue);
                            condition.setValue(value);
                        }
                    } else {
                        condition.setCompareType(compareType);
                        condition.setValue(value);
                    }
                } else if (actionColumns.contains(cell.getColumnIndex())) {
                    Action<V> action = new Action<>();
                    rule.getActions().add(action);
                    action.setField(actionMap.get(cell.getColumnIndex()).getField());
                    action.setParameterPath(actionMap.get(cell.getColumnIndex()).getParameterPath());
                    action.setParameterType(actionMap.get(cell.getColumnIndex()).getType());
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
        return result;
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
