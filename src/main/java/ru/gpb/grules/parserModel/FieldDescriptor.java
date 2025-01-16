package ru.gpb.grules.parserModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* class for describe fields
have been red from excel
 */
public class FieldDescriptor {

    private final Class<?> type;
    private final Field field;
    private final List<String> parameterPath = new ArrayList<>();


    public FieldDescriptor(String path) throws ClassNotFoundException, NoSuchFieldException {
        int slash = path.indexOf('/');
        String dirName = path.substring(0,slash );
        int dot = path.indexOf('.', slash);
        String className = path.substring(slash + 1, dot);
        String fieldName = path.substring(dot + 1);
        String[] splitPath = fieldName.split("\\.");
        parameterPath.add(splitPath[0]);
        type = Class.forName(String.format("%s.%s", dirName, className));
        Field field =type.getDeclaredField(splitPath[0]);
        String[] remain = Arrays.copyOfRange(splitPath, 1, splitPath.length);
        if (remain.length == 0) {
            this.field = field;
        } else {
            this.field =  getField(field, remain);
        }
    }

    private Field getField(Field field, String[] path) throws NoSuchFieldException {
        Field firstField = field.getType().getDeclaredField(path[0]);
        parameterPath.add(path[0]);
        String[] remain = Arrays.copyOfRange(path, 1, path.length);
        if (remain.length == 0) {
            return firstField;
        } else {
            return getField(firstField, remain);
        }
    }

    public List<String> getParameterPath() {
        return parameterPath;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

}
