package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

public class Utils {

    public static Type getValueType(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:  {
                return String.class;
            }
            case NUMERIC: {
                return double.class;
            }
            case BOOLEAN:  {
                return boolean.class;
            }
            case FORMULA:  {
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:  {
                        return String.class;
                    }
                    case NUMERIC: {
                        return double.class;
                    }
                    case BOOLEAN:  {
                        return boolean.class;
                    }
                }
            }
        }
        return null;
    }

    public static Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:  {
                return cell.getStringCellValue();
            }
            case NUMERIC: {
                return cell.getNumericCellValue();
            }
            case BOOLEAN:  {
                return cell.getBooleanCellValue();
            }
            case FORMULA:  {
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:  {
                        return cell.getStringCellValue();
                    }
                    case NUMERIC: {
                        return cell.getNumericCellValue();
                    }
                    case BOOLEAN:  {
                        return cell.getBooleanCellValue();
                    }
                }
            }
        }
        return null;
    }

}
