import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static int variant;
    public static String amount;
    public static String language;
    public static String currency;
    public static String pennies = "TEXT";

    public static void main(String[] args) {

        System.out.println("Hi, please select a language:\n" + "1: UKR\t" + "2: ENG");
        Scanner in = new Scanner(System.in);
        Scanner sum = new Scanner(System.in);
        variant = in.nextInt();
        if (variant == 1) {
            System.out.println("Введіть, будь ласка, Вашу сумму : ");
            language = "UKR";
            currency = "UAH";
        } else if (variant == 2) {
            System.out.println("Please enter the currency: ");
            language = "ENG";
            currency = "USD";
        } else {
            System.out.println("Sorry, try again, " + "please select a language:\n" + "1: UKR\t" + "2: ENG");
        }

        amount = sum.nextLine().replace(",", ".");

        String result = new moneyToString(Currency.valueOf(currency), Language.valueOf(language), Pennies.valueOf(pennies)).convert(Double.valueOf(amount));
        System.out.println(result);
    }
}
