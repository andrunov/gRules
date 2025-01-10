package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Sheet;
import ruleEngine.BaseRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRuleParser extends BaseSheetParser {

    protected int priorityRow;
    protected String ruleName;
    protected final int firstRow;
    protected int lastRow;
    protected List<Integer> conditionRows = new ArrayList<>();

    public BaseRuleParser(File file, Sheet sheet, List<CellRange<?>> ranges, int startRow) {
        super(file, sheet);
        super.ranges = ranges;
        this.firstRow = startRow;
    }



}
