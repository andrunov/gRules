package parserModel;

import java.io.File;

/* consist base fields and logic
necessary for read single excel book
and all constants */
public abstract class BaseExcelParser {

    protected static final String LINE_RULE = "#linerule";
    protected static final String TABLE_RULE = "#tablerule";
    protected static final String HEAD = "#head";
    protected static final String PRIORITY = "#priority";
    protected static final String CONDITION= "#condition";
    protected static final String ACTION = "#action";
    protected static final String PATH = "#path";
    protected static final String RULE_ROW = "#row";
    protected static final String END = "#end";

    protected final File file;

    public BaseExcelParser(File file) {
        this.file = file;
    }
}
