package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/* consist all common fields and logic
necessary for read single excel book
and all constants */
public abstract class BaseExcelParser {

    protected static final String LINE_RULE = "#linerule";
    protected static final String TABLE_RULE = "#tablerule";
    protected static final String HEAD = "#head";
    protected static final String PRIORITY = "#priority";
    protected static final String CONDITION= "#condition";
    protected static final String ACTION = "#action";
    protected static final String PATH = "#path";
    protected static final String RULE_ROW = "#row";
    protected static final String END = "#end";

    protected final File file;

    public BaseExcelParser(File file) {
        this.file = file;
    }

    protected Type getValueType(Cell cell) {
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

    protected Object getValue(Cell cell) {
        Object result = null;
        if (cell != null && cell.getCellType() != CellType.BLANK) {
            //System.out.printf("Sheet:[%.20s] Cell:[%s] ", cell.getSheet().getSheetName(),cell.getAddress());
            switch (cell.getCellType()) {
                case STRING: {
                    result = castToDate(cell.getStringCellValue());
                    break;
                }
                case NUMERIC: {
                    result = cell.getNumericCellValue();
                    break;
                }
                case BOOLEAN: {
                    result = cell.getBooleanCellValue();
                    break;
                }
                case FORMULA: {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING: {
                            result = castToDate(cell.getStringCellValue());
                            break;
                        }
                        case NUMERIC: {
                            result = cell.getNumericCellValue();
                            break;
                        }
                        case BOOLEAN: {
                            result = cell.getBooleanCellValue();
                            break;
                        }
                    }
                }
            }
            //System.out.printf("Value:[%s] success\n", result);
        }
        return result;
    }

    protected Object castToDate(String value) {
        Object result = value;
        if (value.startsWith("date")) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            try {
                Date date = format.parse(value.substring(5));
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                result = calendar;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    protected Object castTo(String value) {
        Object result = castToBoolean(value);
        if (result.getClass().equals(String.class)) {
            result = castToDouble(value);
        }
        if (result.getClass().equals(String.class)) {
            result = castToDate(value);
        }
        return result;
    }

    protected Object castToBoolean(String value) {
        Object result = value;
        if (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("false")) {
            result = Boolean.parseBoolean(value);
        }
        return result;
    }

    protected Object castToDouble(String value) {
        Object result = value;
        try {
            result = Double.parseDouble(value);
        } catch (Exception e) {

        }
        return result;
    }

    protected String removeRowSplitters(String string) {
        char[] chars = string.toCharArray();
        List<Character> listCh = new ArrayList<>();
        for (char ch : chars) {
            if (ch != '\n') {
                listCh.add(ch);
            }
        }
        char[] result = new char[listCh.size()];
        int i = 0;
        for (char ch : listCh) {
            result[i] = ch;
            i++;
        }
        return new String(result);
    }

    protected Object parseEnum(String strValue, Field field) {
        Class<?> clazz = field.getType();
        if (clazz.isEnum()) {
            for (Object enumVal : field.getType().getEnumConstants()) {
                if (enumVal.toString().equals(strValue)) {
                    return enumVal;
                }
            }
        }
        return null;
    }


}
