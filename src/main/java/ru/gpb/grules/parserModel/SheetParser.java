package ru.gpb.grules.parserModel;

import ru.gpb.grules.exception.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import ru.gpb.grules.ruleEngine.BaseRule;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/* starts read excel sheet
and then delegate it to rule parsers
 */
public class SheetParser extends BaseSheetParser {

    private static final Logger LOG = LogManager.getLogger();

    public SheetParser(File file, Sheet sheet) {
        super(file, sheet);
    }

    public List<BaseRule> readSheet() throws ClassNotFoundException, NoSuchFieldException, ParseException {
        initRanges();
        LOG.info(String.format("Read sheet: %s", sheet.getSheetName()));
        return readSheet(sheet.getFirstRowNum());
    }

    private void initRanges() throws ClassNotFoundException {
        ranges = new ArrayList<>();
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            Cell firstCell = sheet.getRow(range.getFirstRow()).getCell(range.getFirstColumn());
            Type valueType = getValueType(firstCell);
            Object value = getValue(firstCell);
            assert valueType != null;
            Class<?> theClass = Class.forName(valueType.getTypeName());
            ranges.add(new CellRange<>(range, valueType, theClass.cast(value)));
        }
    }


    private List<BaseRule> readSheet(int startRowNum) throws NoSuchFieldException, ClassNotFoundException, ParseException {
        List<BaseRule> result = new ArrayList<>();
        for (int i = startRowNum; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                Object value = getWithFinding(cell);
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
