package ruleEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public abstract class LogicAtom implements Applyable{

    protected List<String> parameterPath;

    protected Type parameterType;

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

    public boolean selectAndApply(Object ... parameters) {
        boolean result = false;
        Object parameter = select(parameters);
        if (parameter != null) {
            result = apply(parameter);
        }
        return result;
    }

    @Override
    public Object extract(Object globalParameter, int startIndex, int depth) throws NoSuchFieldException, IllegalAccessException {
        Field field = globalParameter.getClass().getDeclaredField(parameterPath.get(startIndex));
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object obj = field.get(globalParameter);
        if (parameterPath.size() == startIndex + 1 + depth) {
            return obj;
        } else {
            return extract(obj, startIndex + 1, depth);
        }
    }
}
