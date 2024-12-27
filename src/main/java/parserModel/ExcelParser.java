package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.Performable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    public List<Performable> readFiles(String path) throws IOException, ParseException, NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(file);
        Iterator<Sheet> iterator = workbook.sheetIterator();
        List<Performable> performables = new ArrayList<>();
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            SheetlParser parser = new SheetlParser(sheet);
            performables.addAll(parser.readSheet());
        }
        return performables;
    }
}
