import businessModel.CreditRequest;
import exception.ParseException;
import parserModel.ExcelParser;
import ruleEngine.Rule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, ParseException, NoSuchFieldException, NoSuchMethodException {

        ExcelParser parser = new ExcelParser();
        String dir = System.getProperty("user.dir");
        parser.readSheet(dir +  "\\Rule_01.xlsx");
        CreditRequest creditRequest = new CreditRequest("1.24.01", 100000, false);
        System.out.println();
        System.out.println(creditRequest);
        for (Rule rule : parser.getRuleMap().values()) {
            rule.perform(creditRequest);
        }
        System.out.println(creditRequest);






    }
}
