package parserModel;

import exception.ParseException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ruleEngine.BaseRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* starts read excel file
and then delegate it to sheet parser
 */
public class ExcelParser extends BaseExcelParser {

    public ExcelParser(File file) {
        super(file);
    }

    public List<BaseRule> readFile() throws IOException, NoSuchFieldException, ClassNotFoundException, ParseException {
        FileInputStream stream = new FileInputStream(file);
        System.out.println("Read file: " + file);
        Workbook workbook = new XSSFWorkbook(stream);
        Iterator<Sheet> iterator = workbook.sheetIterator();
        List<BaseRule> performables = new ArrayList<>();
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            SheetParser parser = new SheetParser(file, sheet);
            performables.addAll(parser.readSheet());
        }
        System.out.println("Read file finished");
        System.out.println();
        return performables;
    }
}
