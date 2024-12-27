package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import ruleEngine.Performable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SheetlParser {

    private static final String RULE = "#rule";

    private Sheet sheet;

    private List<CellRange<?>> ranges;

    public SheetlParser(Sheet sheet) {
        this.sheet = sheet;
    }

    public List<Performable> readSheet() throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        initRanges();
        return readRules(sheet.getFirstRowNum());
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


    private List<Performable> readRules(int startRowNum) throws NoSuchFieldException, ClassNotFoundException {
        List<Performable> result = new ArrayList<>();
        for (int i = startRowNum; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = Utils.getValue(cell);
                if (value == null) {
                    value = lookUpАorRanges(cell);
                }
                if (value != null && value.getClass().equals(String.class)) {

                    if (value.equals(RULE)) {

                        int startRow = row.getRowNum();
                        RulelParser rulelParser = new RulelParser(sheet, ranges, startRow);
                        result.add(rulelParser.readSheet());
                        i = rulelParser.getLastRow();

                    }
                }
            }
        }
        return result;
    }
}
