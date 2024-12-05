package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;
import java.util.List;

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
        if (cell == null) {
            return null;
        }
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

    public static Object castTo(String val) {
        Object result = val;
        if (val.equalsIgnoreCase("true")
                || val.equalsIgnoreCase("false")) {
            result = Boolean.parseBoolean(val);
        } else {
            try {
                result = Double.parseDouble(val);
            } catch (Exception e) {

            }
        }
        return result;
    }



}
