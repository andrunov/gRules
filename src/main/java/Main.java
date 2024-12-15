import businessModel.Borrower;
import businessModel.BorrowerType;
import businessModel.CreditProgram;
import businessModel.CreditRequest;
import exception.ParseException;
import parserModel.ExcelParser;
import ruleEngine.Performable;
import ruleEngine.Rule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {

        CreditRequest creditRequest = new CreditRequest("1.24.01", 350000);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        System.out.println();
        System.out.println(creditRequest);
        CreditProgram creditProgram = new CreditProgram();
        System.out.println(creditProgram);
        System.out.println(" ---  RULES REFORMATION STARTED ---");

        ExcelParser parser = new ExcelParser();
        String dir = System.getProperty("user.dir");
        List<Performable> performables = parser.readSheet(dir +  "\\Rule_01.xlsx");
        for (Performable performable : performables) {
            performable.perform(creditRequest, creditProgram);
        }

        System.out.println(" ---  RULES REFORMATION FINISHER ---");
        System.out.println(creditRequest);
        System.out.println(creditProgram);






    }
}
