package bot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Program {
    public static void main(String[] argv){

        Bot bot = new Bot();
        bot.Start();
    }

    private static Date GetCorrectDate(String strDate, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date;
        try {
            date = format.parse(strDate);
        }
        catch (ParseException e) {
            System.out.println("Неверный формат даты/времени. [" + strDate + "]");
            return null;
        }
        return date;
    }


}