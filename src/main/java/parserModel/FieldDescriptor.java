package parserModel;

import java.lang.reflect.Field;

public class FieldDescriptor {

    private String className;

    private String fieldName;

    public FieldDescriptor(String className) {
        this.className = className;
    }

    public Class<?> extractClass() throws ClassNotFoundException {
        return Class.forName(className);
    }

    public Field extractField() throws ClassNotFoundException, NoSuchFieldException {
        return extractClass().getDeclaredField(fieldName);
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

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
