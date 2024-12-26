package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.Performable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser {

    private static final String RULE = "#rule";

    private Sheet sheet;

    private List<CellRange<?>> ranges;

    public List<Performable> readSheet(String path) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheetAt(0);
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
