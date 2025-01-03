package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.BaseRule;
import ruleEngine.Performable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    private final File file;

    public ExcelParser(File file) {
        this.file = file;
    }

    public List<BaseRule> readFile() throws IOException, NoSuchFieldException, ClassNotFoundException {
        FileInputStream stream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(stream);
        Iterator<Sheet> iterator = workbook.sheetIterator();
        List<BaseRule> performables = new ArrayList<>();
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            SheetlParser parser = new SheetlParser(file, sheet);
            performables.addAll(parser.readSheet());
        }
        return performables;
    }
}
