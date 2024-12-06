package parserModel;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldDescriptor {

    private String dirName;
    private String className;

    private String fieldName;

    public FieldDescriptor(String className) {
        this.className = className;
    }

    public Class<?> extractClass() throws ClassNotFoundException {
        return Class.forName(String.format("%s.%s",dirName,className));
    }

    public Field extractField() throws NoSuchFieldException, ClassNotFoundException {
        String[] splitPath = fieldName.split("\\.");
        Field field =extractClass().getDeclaredField(splitPath[0]);
        String[] remain = Arrays.copyOfRange(splitPath, 1, splitPath.length);
        if (remain.length == 0) {
            return field;
        } else {
            return getField(field, remain);
        }
    }

    private Field getField(Field field, String[] path) throws NoSuchFieldException {
        Field firstField = field.getDeclaringClass().getDeclaredField(path[0]);
        String[] remain = Arrays.copyOfRange(path, 1, path.length);
        if (remain.length == 0) {
            return firstField;
        } else {
            return getField(firstField, remain);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
