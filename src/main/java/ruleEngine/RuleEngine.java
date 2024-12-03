package ruleEngine;

import businessModel.CreditRequest;
import exception.ParseException;

import java.lang.reflect.InvocationTargetException;

public class RuleEngine {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException {
        CreditRequest creditRequest = new CreditRequest("1.24.01", 100000, true);
       // Condition<String, String> condition = new Condition<>(creditRequest,"programCode", CompareType.fromString("="), "1.24.01");
       // System.out.println(condition.apply());
       // Action<String, String> action = new Action<>(creditRequest, ".setProgramCode","125");
        Action<String, String> action = new Action<>(creditRequest, "programCode","125");
        System.out.println(creditRequest.getProgramCode());
        action.apply();
        System.out.println(creditRequest.getProgramCode());

    }
}
