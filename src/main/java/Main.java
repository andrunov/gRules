import businessModel.Borrower;
import businessModel.BorrowerType;
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
        CreditRequest creditRequest = new CreditRequest("1.24.01", 350000);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        System.out.println();
        System.out.println(creditRequest);
        for (Rule rule : parser.getRuleMap().values()) {
            rule.perform(creditRequest);
        }
        System.out.println(creditRequest);






    }
}
