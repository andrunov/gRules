package parserModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldDescriptor {

    public static FieldDescriptor from(String path) {
        int slash = path.indexOf('/');
        String dir = path.substring(0,slash );
        int dot = path.indexOf('.');
        String className = path.substring(slash + 1, dot);
        String field = path.substring(dot + 1);
        FieldDescriptor result = new FieldDescriptor(className);
        result.setDirName(dir);
        result.setFieldName(field);
        return result;
    }

    private String dirName;
    private String className;

    private Class type;
    private String fieldName;
    private Field field;
    private List<String> parameterPath = new ArrayList<>();


    public FieldDescriptor(String className) {
        this.className = className;
    }

    public Class<?> extractClass() throws ClassNotFoundException {
        return Class.forName(String.format("%s.%s",dirName,className));
    }

    public void extractFieldAndPar() throws NoSuchFieldException, ClassNotFoundException {
        String[] splitPath = fieldName.split("\\.");
        parameterPath.add(splitPath[0]);
        Field field =extractClass().getDeclaredField(splitPath[0]);
        type = Class.forName(String.format("%s.%s", dirName, className));
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

    public List<String> getParameterPath() {
        return parameterPath;
    }

    public void setParameterPath(List<String> parameterPath) {
        this.parameterPath = parameterPath;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
