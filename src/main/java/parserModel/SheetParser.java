package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import ruleEngine.BaseRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SheetParser {

    private static final String TABLE_RULE = "#tablerule";

    private final File parentFile;
    private final Sheet sheet;

    private List<CellRange<?>> ranges;

    public SheetParser(File parentFile, Sheet sheet) {
        this.parentFile = parentFile;
        this.sheet = sheet;
    }

    public List<BaseRule> readSheet() throws ClassNotFoundException, NoSuchFieldException {
        initRanges();
        return readSheet(sheet.getFirstRowNum());
    }

    private void initRanges() throws ClassNotFoundException {
        ranges = new ArrayList<>();
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
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


    private List<BaseRule> readSheet(int startRowNum) throws NoSuchFieldException, ClassNotFoundException {
        List<BaseRule> result = new ArrayList<>();
        for (int i = startRowNum; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(TABLE_RULE)) {

                        int startRow = row.getRowNum();
                        TableRuleParser tableRuleParser = new TableRuleParser(parentFile, sheet, ranges, startRow);
                        result.add(tableRuleParser.readRule());
                        i = tableRuleParser.getLastRow();

                    }
                }
            }
        }
        return result;
    }
}
