import parserModel.ExcelParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ExcelParser parser = new ExcelParser();
        String dir = System.getProperty("user.dir");
        parser.readConditions(dir +  "\\Rule_01.xlsx");
        System.out.println(parser.toString());

    }
}
