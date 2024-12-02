package ruleEngine;

import businessModel.CreditRequest;

import java.lang.reflect.InvocationTargetException;

public class RuleEngine {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CreditRequest creditRequest = new CreditRequest("1.24.01", 100000, true);
       // Condition<String, String> condition = new Condition<>(creditRequest,"programCode", CompareType.fromString("="), "1.24.01");
       // System.out.println(condition.apply());
        Action<String, String> action = new Action<>(creditRequest, "setProgramCode","125");
        System.out.println(creditRequest.getProgramCode());
        action.apply();
        System.out.println(creditRequest.getProgramCode());

    }
}
