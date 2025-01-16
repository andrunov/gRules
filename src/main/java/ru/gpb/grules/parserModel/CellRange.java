package ru.gpb.grules.parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Type;

/* describes range of cell
and some logic for work with it
 */
public class CellRange<T>  {

    private final CellRangeAddress address;
    private final Type valueType;
    private final T value;

    public CellRange(CellRangeAddress address, Type valueType, T value) {
        this.address = address;
        this.valueType = valueType;
        this.value = value;
    }


    public CellRangeAddress getAddress() {
        return address;
    }

    public Type getValueType() {
        return valueType;
    }

    public T getValue() {
        return value;
    }

    public boolean contains (Cell cell) {
        return address.isInRange(cell);
    }
}
