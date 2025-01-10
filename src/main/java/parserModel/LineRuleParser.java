package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* reads LineRule class from excel
 */
public class LineRuleParser extends BaseRuleParser {

    private List<Integer> actionRows = new ArrayList<>();

    public LineRuleParser(File file, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        super(file, sheet, ranges,startRow);
    }

    public int getLastRow() {
        return lastRow;
    }

    public BaseRule readRule() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        initFields();
        LineRule result = new LineRule(getPriority(), file, sheet, ruleName, "Строка " + (firstRow + 1));
        result.setConditions(readConditions());
        result.setActions(readActions());
        return result;
    }

    protected void readFirstColumn() throws ParseException {
        for (int i = firstRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = getValue(cell);
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


    private <V extends Comparable<V>> List<Action<?>> readActions() throws NoSuchFieldException, ClassNotFoundException {
        List<Action<?>> result = new ArrayList<>();
        for (int rowNumber : actionRows) {
            Cell cell = sheet.getRow(rowNumber).getCell(1);
            Object expression = getValue(cell);
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
                Object enumValue = parseEnum(value, action.getField());
                if (enumValue != null) {
                    action.setValue((V) enumValue);
                } else {
                    action.setValue((V) castTo(value));
                }
                result.add(action);
            }
        }
        return result;
    }





}
