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

    private Map<Integer, List<String>> conditionMap;
    private Map<Integer, Rule> ruleMap;

    int leftBorder = 0;
    int rightBorder = 0;
    int upBorder = 0;
    int downBorder = 0;

    public Map<Integer, Rule> getRuleMap() {
        return ruleMap;
    }

    public  <V extends Comparable> void readConditions(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<CellRange<?>> ranges = new ArrayList<>();
        Map<Integer, String> keyMap = new HashMap<>();
        conditionMap = new HashMap<>();
        ruleMap = new HashMap<>();


        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
        }

        leftBorder = 0;
        rightBorder = 0;
        upBorder = -1;
        downBorder = 10000000;

        for (Row row : sheet) {

            Rule rule = null;
            if (acceptRow(row)) {
                conditionMap.put(row.getRowNum(), new ArrayList<>());
                rule = new Rule("Строка " + row.getRowNum());
                ruleMap.put(row.getRowNum(), rule);
            }

            int columnCounter = 0;

            for (Cell cell : row) {

                V value = null;
                boolean isSlave = false;
                if (cell.getCellType() == CellType.BLANK) {

                    for (CellRange<?> range : ranges) {
                        if (range.contains(cell)) {
                            value = (V) range.getValue();
                            columnCounter++;
                            isSlave = true;
                            break;
                        }
                    }
                }

                if (!isSlave) {
                    value = (V) Utils.getValue(cell);
                    if (value instanceof String) {
                        if (value.equals("start table")) {
                            leftBorder = cell.getColumnIndex();
                            upBorder = cell.getRowIndex() + 1;
                        } else if (value.equals("end column")) {
                            rightBorder = cell.getColumnIndex() + 1;
                        } else if (value.equals("end table")) {
                            downBorder = cell.getRowIndex();
                        }
                    }
                    columnCounter++;
                }


                if (acceptKey(cell)) {
                    keyMap.put(columnCounter, (String) value);
                } else if (acceptValue(cell)) {

                    if (columnCounter < keyMap.size()) {
                        //conditions
                        //String strCondition = String.format("%-15.15s   %-15.15s", keyMap.get(columnCounter), value);
                        //conditionMap.get(row.getRowNum()).add(strCondition);
                        Condition<V> condition = new Condition<>();
                        rule.getConditions().add(condition);
                        condition.setParameterName(keyMap.get(columnCounter));
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
                        action.setMethodOrField(keyMap.get(columnCounter));
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
            conditionMap.remove(downBorder);
            ruleMap.remove(downBorder);

        }
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Integer> conditionNumbers = new ArrayList<>(conditionMap.keySet());
        conditionNumbers.sort((o1, o2) -> o1 - o2);
        for (Integer integer : conditionNumbers) {
            sb.append(String.format("Row %s:\n", integer));
            sb.append("--------------------------------\n");
            List<String> conditions = conditionMap.get(integer);
            for (String condition : conditions) {
                sb.append(String.format("%s\n",condition));
            }
            sb.append("================================\n");
        }
        return sb.toString();
    }
}
