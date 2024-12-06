import exception.ParseException;
import parserModel.ExcelParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, ParseException, NoSuchFieldException, NoSuchMethodException {

        Class<?> tClass = Class.forName("businessModel.CreditRequest");
        Field field = tClass.getDeclaredField("programCode");
        System.out.println(field);

        /*
        ExcelParser parser = new ExcelParser();
        String dir = System.getProperty("user.dir");
        parser.readSheet(dir +  "\\Rule_01.xlsx");

         */
        /*
        parser.readConditions(dir +  "\\Rule_01.xlsx");
        CreditRequest creditRequest = new CreditRequest("1.24.03", 200000, true);
        System.out.println(creditRequest);
        for (Rule rule : parser.getRuleMap().values()) {
            rule.perform(creditRequest);
        }
        System.out.println(creditRequest);

         */





    }
}
