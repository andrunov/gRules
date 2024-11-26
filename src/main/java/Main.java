import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import parserModel.CellRange;
import parserModel.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String dir = System.getProperty("user.dir");
        FileInputStream file = new FileInputStream(new File(dir + "\\Rule_01.xlsx"));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<CellRange<?>> ranges = new ArrayList<>();
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            ranges.add(CellRange.of(range, sheet));
        }

        for (Row row : sheet) {
            System.out.println("Row: " + row.getRowNum());
            for (Cell cell : row) {

                boolean isSlave = false;
                if (cell.getCellType() == CellType.BLANK) {

                    for (CellRange<?> range : ranges) {
                        if (range.contains(cell)) {

                            System.out.println(range.getValue());
                            isSlave = true;
                            break;
                        }
                    }
                }

                if (!isSlave) {
                    System.out.println(Utils.getValue(cell));
                }


            }
            System.out.println();
        }
    }


}
