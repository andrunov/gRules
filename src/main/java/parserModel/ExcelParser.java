package parserModel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelParser {

    private Map<Integer, List<String>> conditionMap;

    int leftBorder = 0;
    int rightBorder = 0;
    int upBorder = 0;
    int downBorder = 0;

    public void readConditions(String path) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<CellRange<?>> ranges = new ArrayList<>();
        Map<Integer, String> keyMap = new HashMap<>();
        conditionMap = new HashMap<>();

        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
        }

        leftBorder = 0;
        rightBorder = 0;
        upBorder = -1;
        downBorder = 10000000;

        for (Row row : sheet) {

            if (acceptRow(row)) {
                conditionMap.put(row.getRowNum(), new ArrayList<>());
            }

            int columnCounter = 0;

            for (Cell cell : row) {
                Object value = null;
                boolean isSlave = false;
                if (cell.getCellType() == CellType.BLANK) {

                    for (CellRange<?> range : ranges) {
                        if (range.contains(cell)) {
                            value = range.getValue();
                            columnCounter++;
                            isSlave = true;
                            break;
                        }
                    }
                }

                if (!isSlave) {
                    value = Utils.getValue(cell);
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
                    String condition = String.format("%-15.15s   %-15.15s",keyMap.get(columnCounter) , value);
                    conditionMap.get(row.getRowNum()).add(condition);
                }
            }
            conditionMap.remove(downBorder);
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
