import businessModel.Borrower;
import businessModel.BorrowerType;
import businessModel.CreditProgram;
import businessModel.CreditRequest;
import exception.ParseException;
import parserModel.ExcelParser;
import ruleEngine.Performable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, NoSuchFieldException, NoSuchMethodException {




        CreditRequest creditRequest = new CreditRequest("1.26.01", 350000);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        System.out.println();
        System.out.println(creditRequest);
        CreditProgram creditProgram = new CreditProgram();
        System.out.println(creditProgram);

        System.out.println();
        System.out.println(" ---  RULES REFORMATION STARTED ---");


        String basePath = Main.class.getClassLoader().getResource("Rules").getPath();
        File baseDir = new File(basePath);
        for (File file : baseDir.listFiles()) {
           // System.out.println(file);
            ExcelParser parser = new ExcelParser(file);
            List<Performable> performables = parser.readFile();
            for (Performable performable : performables) {
                performable.perform(creditRequest, creditProgram);
            }

        }

        System.out.println(" ---  RULES REFORMATION FINISHER ---");
        System.out.println();
        System.out.println(creditRequest);
        System.out.println(creditProgram);







    }
}
