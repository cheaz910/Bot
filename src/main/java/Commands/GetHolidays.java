package Commands;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetHolidays {
    private static HashMap<String, String> months;

    static {
        months = new HashMap<>();
        months.put("01", "january");
        months.put("02", "february");
        months.put("03", "march");
        months.put("04", "april");
        months.put("05", "may");
        months.put("06", "june");
        months.put("07", "july");
        months.put("08", "august");
        months.put("09", "september");
        months.put("10", "october");
        months.put("11", "november");
        months.put("12", "december");
    }

    public static String help() {
        return "Введите день и месяц, праздники которого хотите узнать.\n" +
                "Формат ввода: dd.MM";
    }

    public static void doCommand(String strDate, PrintStream outputStream) {
        try {
            if (DateWorker.getCorrectDate(strDate, "dd.MM") == null) {
                outputStream.println("Неверный формат ввода: " + strDate);
                return;
            }
            String url = getUrl(strDate);
            String content = GetHolidays.getContent(url);
            ArrayList<String> holidays = GetHolidays.getHolidays(content);
            for (String holiday : holidays) {
                outputStream.println(holiday);
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getUrl(String strDate){
        String day = strDate.charAt(0) == '0' ? strDate.substring(1, 2) : strDate.substring(0, 2);
        String month = months.get(strDate.substring(3, 5));
        return "https://my-calend.ru/holidays/russia/" + day + "-" + month;
    }

    private static String getContent(String pageAddress) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new URL(pageAddress).openConnection().getInputStream(), "UTF8"));
        try {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }

    private static ArrayList<String> getHolidays(String text){
        String contentWithHolidays = getContentWithHolidays(text);
        if (contentWithHolidays == null){
            return new ArrayList<>();
        }
        Pattern pattern = Pattern.compile("(<li><a href=.+?>)(.+?)(</a>)");
        Matcher matcher = pattern.matcher(contentWithHolidays);
        ArrayList<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        return result;

    }

    private static String getContentWithHolidays(String text) {
        Pattern pattern = Pattern.compile("<ul class=\"holidays-items\">.+?<div class=\"near\">");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return text.substring(matcher.start(), matcher.end());
        }
        else{
            return null;
        }
    }
}