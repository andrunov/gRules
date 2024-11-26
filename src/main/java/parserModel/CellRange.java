package parserModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Type;

public class CellRange<T>  {


    public static CellRange<?> of(CellRangeAddress address, Sheet parentSheet) throws ClassNotFoundException {
        Cell firstCell = parentSheet.getRow(address.getFirstRow()).getCell(address.getFirstColumn());
        Type valueType = Utils.getValueType(firstCell);
        Object value = Utils.getValue(firstCell);
        assert valueType != null;
        Class<?> theClass = Class.forName(valueType.getTypeName());
        return new CellRange<>(address, valueType, theClass.cast(value));
    }

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
