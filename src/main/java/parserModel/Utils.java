package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static Object findInRanges(Cell cell, List<CellRange<?>> ranges) {
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

    public static Object castTo(String value) {
        Object result = castToBoolean(value);
        if (result.getClass().equals(String.class)) {
            result = castToDouble(value);
        }
        if (result.getClass().equals(String.class)) {
            result = castToDate(value);
        }
        return result;
    }

    private static Object castToDate(String value) {
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

    private static Object castToBoolean(String value) {
        Object result = value;
        if (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("false")) {
            result = Boolean.parseBoolean(value);
        }
        return result;
    }

    private static Object castToDouble(String value) {
        Object result = value;
        try {
             result = Double.parseDouble(value);
        } catch (Exception e) {

        }
        return result;
    }

    public static String removeRowSplitters(String string) {
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

    public static Object parseEnum(String strValue, Field field) {
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

    public static String getSimpleName(String path) {
        int lastDot = path.lastIndexOf('.');
        return path.substring(lastDot + 1);
    }

    public String removeLineBreaks(String string) {
        List<Character> characters = new ArrayList<>();
        for (char charr : string.toCharArray()){
            if (charr != '\n') {
                characters.add(charr);
            }
        }
        Character[] newChars = characters.toArray(new Character[0]);
        return Arrays.toString(newChars);
    }



}
