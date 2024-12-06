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
    private static final String DIR = "#dir";
    private static final String CLASS = "#class";
    private static final String FIELD = "#field";
    private static final String VALUE = "#rule";

    private int headRow;
    private List<Integer> dirList = new ArrayList<>();
    private List<Integer> classList = new ArrayList<>();
    private List<Integer> fieldList = new ArrayList<>();
    private List<Integer> ruleList = new ArrayList<>();
    private List<Integer> conditionColumns = new ArrayList<>();
    private List<Integer> actionColumns = new ArrayList<>();
    Map<Integer, FieldDescriptor> conditionMap = new HashMap<Integer, FieldDescriptor>();
    Map<Integer, FieldDescriptor> actionMap = new HashMap<Integer, FieldDescriptor>();
    private Map<Integer, Rule> ruleMap  = new HashMap<>();
    Sheet sheet;
    List<CellRange<?>> ranges;
    public Map<Integer, Rule> getRuleMap() {
        return ruleMap;
    }

    public void readSheet(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheetAt(1);
        initRanges();
        readFirstColumn();
        readHeader();
        readClasses();
        readFields();
        readDirs();
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
                    if (conditionColumns.contains(columnIndex)) {
                        conditionMap.get(columnIndex).setFieldName((String) value);
                    } else if (actionColumns.contains(columnIndex)) {
                        actionMap.get(columnIndex).setFieldName((String) value);
                    }
                }
            }
        }
    }

    private <V extends Comparable> void readRules() throws NoSuchFieldException, ClassNotFoundException {
        for (Integer ruleRow : ruleList) {
            Rule rule = new Rule("Строка " + (ruleRow + 1)); //TODO разобраться откуда лишняя единица в нумерации
            ruleMap.put(ruleRow, rule);

            for (Cell cell : sheet.getRow(ruleRow)) {
                V value = (V) Utils.getValue(cell);
                if (value == null) {
                    value = (V) lookUpАorRanges(cell);
                }

                if (conditionColumns.contains(cell.getColumnIndex())) {
                    Condition<V> condition = new Condition<>();
                    rule.getConditions().add(condition);
                    condition.setField(conditionMap.get(cell.getColumnIndex()).extractField());
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

                    action.setField(actionMap.get(cell.getColumnIndex()).extractField());
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
