package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.*;

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
    private static final String DIR = "#dir";
    private static final String CLASS = "#class";
    private static final String FIELD = "#field";
    private static final String VALUE = "#rule";
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

    Sheet sheet;
    List<CellRange<?>> ranges;



    public List<Performable> readSheet(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheetAt(0);
        initRanges();
        readFirstColumn();
        List<Performable> ruleSheet = new ArrayList<>();
        RuleTable ruleTable = new RuleTable();
        ruleSheet.add(ruleTable);
        ruleTable.setPreConditions(readPreconditions());
        readHeader();
        readClasses();
        readFields();
        readDirs();
        extractParameters();
        ruleTable.setRules(readRules());
        return ruleSheet;
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
                } else if (value.equals(VALUE)) {
                    ruleList.add(row.getRowNum());
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

    private <V extends Comparable<V>> List<Rule> readRules() throws NoSuchFieldException, ClassNotFoundException {
        List<Rule> result = new ArrayList<>();
        for (Integer ruleRow : ruleList) {
            Rule rule = new Rule("Строка " + (ruleRow + 1)); //TODO разобраться откуда лишняя единица в нумерации
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
