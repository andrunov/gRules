package ruleEngine;

import businessModel.Borrower;
import businessModel.CreditRequest;
import exception.ParseException;

import java.lang.reflect.InvocationTargetException;

public class RuleEngine {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException {
        CreditRequest creditRequest = new CreditRequest("1.24.01", 100000, true);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        creditRequest.setBorrower(borrower);
        String name = "borrower.salaryClient";
//        Field field = creditRequest.getClass().getDeclaredField(split[0]);
//        Field field2 = field.getDeclaringClass().getDeclaredField(split[1]);

    }

}
