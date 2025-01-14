package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.List;

/*
consist all common fields and logic
necessary for read single excel list
 */
public abstract class BaseSheetParser extends BaseExcelParser {
    protected final Sheet sheet;
    protected List<CellRange<?>> ranges;

    public BaseSheetParser(File file, Sheet sheet) {
        super(file);
        this.sheet = sheet;
    }

    protected  Object findInRanges(Cell cell) {
        Object result = null;
        if (cell != null && cell.getCellType() == CellType.BLANK) {
            for (CellRange<?> range : ranges) {
                if (range.contains(cell)) {
                    result = range.getValue();
                    //System.out.printf("Sheet:[%.20s] Cell:[%s] Value{%s] success - from range %s\n", cell.getSheet().getSheetName(),cell.getAddress(), result, range.getAddress().toString().substring(40));
                    return result;
                }
            }
        }
        return result;
    }

    protected Object getWithFinding(Cell cell) {
        Object value = getValue(cell);
        if (value == null) {
            value = findInRanges(cell);
        }
        return value;
    }


}
