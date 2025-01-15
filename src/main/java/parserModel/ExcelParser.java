package parserModel;

import exception.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger();
    public ExcelParser(File file) {
        super(file);
    }

    public List<BaseRule> readFile() throws IOException, NoSuchFieldException, ClassNotFoundException, ParseException {
        FileInputStream stream = new FileInputStream(file);
        LOG.info(String.format("Read file: %s", file));
        Workbook workbook = new XSSFWorkbook(stream);
        Iterator<Sheet> iterator = workbook.sheetIterator();
        List<BaseRule> performables = new ArrayList<>();
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            SheetParser parser = new SheetParser(file, sheet);
            performables.addAll(parser.readSheet());
        }
        LOG.info("Read file finished\n");
        return performables;
    }
}
