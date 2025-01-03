package ruleEngine;

import businessModel.Borrower;
import businessModel.BorrowerType;
import businessModel.CreditRequest;
import exception.ParseException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RuleEngine {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException, ClassNotFoundException {
        CreditRequest creditRequest = new CreditRequest("1.24.01", 100000);
        Borrower borrower = new Borrower();
        borrower.setSalaryClient(true);
        borrower.setBorrowerType(BorrowerType.GAZPROM);
        creditRequest.setBorrower(borrower);
        Class<?> clazz = Class.forName("businessModel.CreditRequest");
        Field field1 = clazz.getDeclaredField("borrower");
        field1.setAccessible(true);
        Object obj1 = field1.get(creditRequest);
        Field field = clazz.getDeclaredField("borrower").getType().getDeclaredField("borrowerType");
        field.setAccessible(true);
        Object obj = field.get(obj1);
       // System.out.println(obj.getClass());
        String borType = "GAZPROM";
        Class<?> enumm = obj.getClass();


        if (obj.getClass().equals(BorrowerType.class)) {
            System.out.println(enumm);
            if (obj.equals(borType)) {
                System.out.println("Success !!!");
            }

        }

        System.out.println(obj);

    }

}
