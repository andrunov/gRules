package ruleEngine;

import java.lang.reflect.Field;
import java.util.List;

public abstract class LogicAtom implements Applyable{

    private List<String> parameterPath;

    public List<String> getParameterPath() {
        return parameterPath;
    }

    public void setParameterPath(List<String> parameterPath) {
        this.parameterPath = parameterPath;
    }

    @Override
    public Object extract(Object parameter) throws NoSuchFieldException, IllegalAccessException {
        return extract(parameter, 0);
    }

    private Object extract(Object globalParameter, int index) throws NoSuchFieldException, IllegalAccessException {
        Field field = globalParameter.getClass().getDeclaredField(parameterPath.get(index));
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object obj = field.get(globalParameter);
        if (parameterPath.size() == index + 1) {
            return obj;
        } else {
            return extract(obj, index + 1);
        }
    }
}
