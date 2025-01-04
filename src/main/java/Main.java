import businessModel.Borrower;
import businessModel.BorrowerType;
import businessModel.CreditProgram;
import businessModel.CreditRequest;
import exception.ParseException;
import parserModel.ExcelParser;
import ruleEngine.BaseRule;
import ruleEngine.Performable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException {

        CreditRequest creditRequest = new CreditRequest("1.24.01", 1000000);
        Calendar calendar = new GregorianCalendar(2024,Calendar.JULY, 1);
        creditRequest.setApplicDate(calendar);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        System.out.println();
        System.out.println(creditRequest);
        CreditProgram creditProgram = new CreditProgram();
        System.out.println(creditProgram);

        String basePath = Main.class.getClassLoader().getResource("Rules").getPath();
        File baseDir = new File(basePath);
        List<BaseRule> performables = new ArrayList<>();
        for (File file : baseDir.listFiles()) {
            ExcelParser parser = new ExcelParser(file);
            performables.addAll(parser.readFile());
        }

        System.out.println();
        System.out.println("RULES STARTED");
        System.out.println("------------------");
        Collections.sort(performables);
        for (Performable performable : performables) {
            performable.perform(creditRequest, creditProgram);
        }
        System.out.println("------------------");
        System.out.println("RULES FINISHED");
        System.out.println();
        System.out.println(creditRequest);
        System.out.println(creditProgram);







    }
}
