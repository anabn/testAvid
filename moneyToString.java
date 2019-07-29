import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class moneyToString {
    private static final int INDEX_3 = 3;
    private static final int INDEX_2 = 2;
    private static final int INDEX_1 = 1;
    private static final int INDEX_0 = 0;
    private static final int NUM0 = 0;
    private static final int NUM1 = 1;
    private static final int NUM2 = 2;
    private static final int NUM3 = 3;
    private static final int NUM4 = 4;
    private static final int NUM5 = 5;
    private static final int NUM6 = 6;
    private static final int NUM7 = 7;
    private static final int NUM8 = 8;
    private static final int NUM9 = 9;
    private static final int NUM10 = 10;
    private static final int NUM11 = 11;
    private static final int NUM14 = 14;
    private static final int NUM100 = 100;
    private static final int NUM1000 = 1000;
    private static final int NUM10000 = 10000;

    private final Map<String, String[]> messages = new LinkedHashMap<String, String[]>();
    private static String rubOneUnit;
    private static String rubTwoUnit;
    private static String rubFiveUnit;
    private static String rubSex;
    private static String kopOneUnit;
    private static String kopTwoUnit;
    private static String kopFiveUnit;
    private static String kopSex;
    private final Currency currency;
    private final Language language;
    private final Pennies pennies;

    public static JSONObject readFromJSON (String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Object obj = new JSONParser().parse(reader);
//            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;
            JSONObject currencyList = (JSONObject)jo.get("CurrencyList");
            return currencyList;
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.getErrorType();
        }
        return null;
    }

    public moneyToString(Currency currency, Language language, Pennies pennies) {
        String keyValue = "-value";
        String keyText = "-text";
        String file = "CurrencyList.json";

        if (currency == null) {
            throw new IllegalArgumentException("currency is null");
        }
        if (language == null) {
            throw new IllegalArgumentException("language is null");
        }
        if (pennies == null) {
            throw new IllegalArgumentException("pennies is null");
        }
        this.currency = currency;
        this.language = language;
        this.pennies = pennies;

        String theISOstr = currency.name();
        String theLang = language.name();

        JSONObject currencyList = readFromJSON(file);
        JSONObject lang1 =(JSONObject) currencyList.get(theLang);
        JSONArray item = (JSONArray) lang1.get("item");

        for (Object c: item
                ) {
            JSONObject text = (JSONObject) c;
            String value = (String) text.get(keyValue);
            String des = (String) text.get(keyText);
            messages.put(value, des.split(","));

        }

        JSONArray js = (JSONArray) currencyList.get(theISOstr);
        Iterator<Object> iterator = js.iterator();
        for (Object c: js) {
            JSONObject t = (JSONObject) c;
            String value = (String) t.get("-language");
            while (iterator.hasNext()) {
                if(value != theLang) {
                    rubOneUnit = (String) t.get("-RubOneUnit");
                    rubTwoUnit = (String) t.get("-RubTwoUnit");
                    rubFiveUnit = (String) t.get("-RubFiveUnit");
                    kopOneUnit = (String) t.get("-KopOneUnit");
                    kopTwoUnit = (String) t.get("-KopTwoUnit");
                    kopFiveUnit = (String) t.get("-KopFiveUnit");
                    rubSex = (String) t.get("-RubSex");
                    kopSex = (String) t.get("-KopSex");
                }
                break;
            }
        }

}



    public  String convert(Double theMoney) {
        if (theMoney == null) {
            throw new IllegalArgumentException("theMoney is null");
        }
        Long intPart = theMoney.longValue();
        Long fractPart = Math.round((theMoney - intPart) * NUM100);
        if (currency == Currency.PER1000) {
            fractPart = Math.round((theMoney - intPart) * NUM1000);
        }
        return convert(intPart, fractPart);
    }


    public String convert(Long theMoney, Long theKopeiki) {
        if (theMoney == null) {
            throw new IllegalArgumentException("theMoney is null");
        }
        if (theKopeiki == null) {
            throw new IllegalArgumentException("theKopeiki is null");
        }
        StringBuilder money2str = new StringBuilder();
        Long triadNum = 0L;
        Long theTriad;

        Long intPart = theMoney;
        if (intPart == 0) {
            money2str.append(messages.get("0")[0] + " ");
        }
        do {
            theTriad = intPart % NUM1000;
            money2str.insert(0, triad2Word(theTriad, triadNum, rubSex));
            if (triadNum == 0) {
                if ((theTriad % NUM100) / NUM10 == NUM1) {
                    money2str.append(rubFiveUnit);
                } else {
                    switch (Long.valueOf(theTriad % NUM10).byteValue()) {
                        case NUM1:
                            money2str.append(rubOneUnit);
                            break;
                        case NUM2:
                        case NUM3:
                        case NUM4:
                            money2str.append(rubTwoUnit);
                            break;
                        default:
                            money2str.append(rubFiveUnit);
                            break;
                    }
                }
            }
            intPart /= NUM1000;
            triadNum++;
        } while (intPart > 0);

        if (pennies == Pennies.TEXT) {
            money2str.append(language == Language.ENG ? " and " : " ").append(theKopeiki == 0 ? messages.get("0")[0] + " " : triad2Word(theKopeiki, 0L, kopSex));
        } else {
            money2str.append(" " + (theKopeiki < 10 ? "0" + theKopeiki : theKopeiki) + " ");
        }
        if (theKopeiki >= NUM11 && theKopeiki <= NUM14) {
            money2str.append(kopFiveUnit);
        } else {
            switch ((byte) (theKopeiki % NUM10)) {
                case NUM1:
                    money2str.append(kopOneUnit);
                    break;
                case NUM2:
                case NUM3:
                case NUM4:
                    money2str.append(kopTwoUnit);
                    break;
                default:
                    money2str.append(kopFiveUnit);
                    break;
            }
        }
        return money2str.toString().trim();
    }

    private String triad2Word(Long triad, Long triadNum, String sex) {
        final StringBuilder triadWord = new StringBuilder(NUM100);

        if (triad == 0) {
            return "";
        }

        triadWord.append(concat(new String[]{""}, messages.get("100_900"))[Long.valueOf(triad / NUM100).byteValue()]);
        final Long range10 = (triad % NUM100) / NUM10;
        triadWord.append(concat(new String[]{"", ""}, messages.get("20_90"))[range10.byteValue()]);
        if (language == Language.ENG && triadWord.length() > 0 && triad % NUM10 == 0) {
            triadWord.deleteCharAt(triadWord.length() - 1);
            triadWord.append(" ");
        }

        check2(triadNum, sex, triadWord, triad, range10);
        switch (triadNum.byteValue()) {
            case NUM0:
                break;
            case NUM1:
            case NUM2:
            case NUM3:
            case NUM4:
                if (range10 == NUM1) {
                    triadWord.append(messages.get("1000_10")[triadNum.byteValue() - 1] + " ");
                } else {
                    final Long range = triad % NUM10;
                    switch (range.byteValue()) {
                        case NUM1:
                            triadWord.append(messages.get("1000_1")[triadNum.byteValue() - 1] + " ");
                            break;
                        case NUM2:
                        case NUM3:
                        case NUM4:
                            triadWord.append(messages.get("1000_234")[triadNum.byteValue() - 1] + " ");
                            break;
                        default:
                            triadWord.append(messages.get("1000_5")[triadNum.byteValue() - 1] + " ");
                            break;
                    }
                }
                break;
            default:
                triadWord.append("??? ");
                break;
        }
        return triadWord.toString();
    }

    private void check2(Long triadNum, String sex, StringBuilder triadWord, Long triad, Long range10) {
        final Long range = triad % NUM10;
        if (range10 == 1) {
            triadWord.append(messages.get("10_19")[range.byteValue()] + " ");
        } else {
            switch (range.byteValue()) {
                case NUM1:
                    if (triadNum == NUM1) {
                        triadWord.append(messages.get("1")[INDEX_0] + " ");
                    } else if (triadNum == NUM2 || triadNum == NUM3 || triadNum == NUM4) {
                        triadWord.append(messages.get("1")[INDEX_1] + " ");
                    } else if ("M".equals(sex)) {
                        triadWord.append(messages.get("1")[INDEX_2] + " ");
                    } else if ("F".equals(sex)) {
                        triadWord.append(messages.get("1")[INDEX_3] + " ");
                    }
                    break;
                case NUM2:
                    if (triadNum == NUM1) {
                        triadWord.append(messages.get("2")[INDEX_0] + " ");
                    } else if (triadNum == NUM2 || triadNum == NUM3 || triadNum == NUM4) {
                        triadWord.append(messages.get("2")[INDEX_1] + " ");
                    } else if ("M".equals(sex)) {
                        triadWord.append(messages.get("2")[INDEX_2] + " ");
                    } else if ("F".equals(sex)) {
                        triadWord.append(messages.get("2")[INDEX_3] + " ");
                    }
                    break;
                case NUM3:
                case NUM4:
                case NUM5:
                case NUM6:
                case NUM7:
                case NUM8:
                case NUM9:
                    triadWord.append(concat(new String[]{"", "", ""}, messages.get("3_9"))[range.byteValue()] + " ");
                    break;
                default:
                    break;
            }
        }
    }

    private <T> T[] concat(T[] first, T[] second) {
        final T[] result = java.util.Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}