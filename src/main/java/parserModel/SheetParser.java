package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import ruleEngine.BaseRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SheetParser extends BaseSheetParser {

    public SheetParser(File file, Sheet sheet) {
        super(file, sheet);
    }

    public List<BaseRule> readSheet() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        initRanges();
        System.out.println("Read sheet: " + sheet.getSheetName());
        return readSheet(sheet.getFirstRowNum());
    }

    private void initRanges() throws ClassNotFoundException {
        ranges = new ArrayList<>();
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
        }
    }


    private List<BaseRule> readSheet(int startRowNum) throws NoSuchFieldException, ClassNotFoundException, ParseException {
        List<BaseRule> result = new ArrayList<>();
        for (int i = startRowNum; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = findInRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(TABLE_RULE)) {

                        int startRow = row.getRowNum();
                        TableRuleParser tableRuleParser = new TableRuleParser(file, sheet, ranges, startRow);
                        result.add(tableRuleParser.readRule());
                        i = tableRuleParser.getLastRow();

                    } else if (value.equals(LINE_RULE)) {

                        int startRow = row.getRowNum();
                        LineRuleParser lineRuleParser = new LineRuleParser(file, sheet, ranges, startRow);
                        result.add(lineRuleParser.readRule());
                        i = lineRuleParser.getLastRow();

                    }
                }
            }
        }
        return result;
    }
}
