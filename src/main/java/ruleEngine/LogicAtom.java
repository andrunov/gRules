package ruleEngine;

import parserModel.FieldDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

public abstract class LogicAtom implements Applyable{

    protected Field field;
    protected List<String> parameterPath;
    protected Type parameterType;

    public LogicAtom (FieldDescriptor fieldDescriptor) {
        this.field = fieldDescriptor.getField();
        this.parameterPath = fieldDescriptor.getParameterPath();
        this.parameterType = fieldDescriptor.getType();
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<String> getParameterPath() {
        return parameterPath;
    }

    public void setParameterPath(List<String> parameterPath) {
        this.parameterPath = parameterPath;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public void setParameterType(Type parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public Object select(Object ... parameters) {
        Object result = null;
        for (Object parameter : parameters) {
            if (parameter.getClass().getName().equals(((Class<?>) parameterType).getName())) {
                result = parameter;
                break;
            }
        }
        return result;
    }

    public boolean selectAndApply(Object ... parameters) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        boolean result = false;
        Object parameter = select(parameters);
        if (parameter != null) {
            result = apply(parameter);
        }
        return result;
    }

    @Override
    public Object extract(Object globalParameter, int depth) throws NoSuchFieldException, IllegalAccessException {
        return extract(globalParameter, 0, depth);
    }

    private Object extract(Object globalParameter, int startIndex, int depth) throws NoSuchFieldException, IllegalAccessException {
        Field field = globalParameter.getClass().getDeclaredField(parameterPath.get(startIndex));
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object obj = field.get(globalParameter);
        if (parameterPath.size() == startIndex + 1) {
            /* depth is meaning
            0 - return field last founded in hierarchy
            1 - return Object last founded in hierarchy
             */
            if (depth == 0) {
                return obj;
            } else if (depth == 1) {
                return globalParameter;
            }
            return null;
        } else {
            return extract(obj, startIndex + 1, depth);
        }
    }

    protected String getSimpleName(String path) {
        int lastDot = path.lastIndexOf('.');
        return path.substring(lastDot + 1);
    }
}
