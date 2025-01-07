import businessModel.Borrower;
import businessModel.BorrowerType;
import businessModel.CreditProgram;
import businessModel.CreditRequest;
import parserModel.ExcelParser;
import ruleEngine.BaseRule;
import ruleEngine.Performable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    List<BaseRule> rules = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException {
        Main main = new Main();
        main.readDir();
        CreditRequest creditRequest = main.initRequest();
        while (!creditRequest.isComplete()) {
            main.complete(creditRequest);
            System.out.println(creditRequest);
            if (creditRequest.isComplete()) {
                main.perform(creditRequest);
                main.clean(creditRequest);
            }
        }
    }

    private CreditRequest initRequest() {
        CreditRequest creditRequest = new CreditRequest();
        Calendar calendar = new GregorianCalendar(2024,Calendar.JULY, 1);
        creditRequest.setApplicDate(calendar);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        return creditRequest;
    }

    void readDir() throws IOException, NoSuchFieldException, ClassNotFoundException {
        String basePath = Main.class.getClassLoader().getResource("Rules").getPath();
        File baseDir = new File(basePath);
        for (File file : baseDir.listFiles()) {
            ExcelParser parser = new ExcelParser(file);
            rules.addAll(parser.readFile());
        }
    }

    void perform(CreditRequest creditRequest) {
        CreditProgram creditProgram = new CreditProgram();
        System.out.println();
        System.out.println("RULES STARTED");
        System.out.println("------------------");
        Collections.sort(rules);
        for (Performable performable : rules) {
            performable.perform(creditRequest, creditProgram);
        }
        System.out.println("------------------");
        System.out.println("RULES FINISHED");
        System.out.println();
        System.out.println(creditProgram);
    }


    private void complete(CreditRequest creditRequest) {
        Scanner in = new Scanner(System.in);
        if (creditRequest.getProgramCode() == null || creditRequest.getProgramCode().isEmpty()) {
            System.out.print("Введите номер программы: ");
            String prog = in.next();
            creditRequest.setProgramCode(prog);
        } else if (creditRequest.getCreditQty() == 0) {
            System.out.print("Введите сумму кредита: ");
            int qty = in.nextInt();
            creditRequest.setCreditQty(qty);
        }
    }

    private void clean(CreditRequest creditRequest) {
        creditRequest.setProgramCode(null);
        creditRequest.setCreditQty(0);
    }

}
