package Commands;

import Data.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DateWorker {
    static Date getCorrectDate(String strDate, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date;
        try {
            date = format.parse(strDate);
        }
        catch (ParseException e) {
            return null;
        }
        return date;
    }

    static Date complementDate(String strDate) {
        String pattern = "HH:mm-dd.MM.yyyy";
        switch(strDate.length()) {
            case 16:
                return getCorrectDate(strDate, pattern);
            case 11:
                Date interimDate = getCorrectDate(strDate, "HH:mm-dd.MM");
                if (interimDate == null) { return null; }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                return getCorrectDate(strDate + "." + year, pattern);
            case 8:
                Date interimDate2 = getCorrectDate(strDate, "HH:mm-dd");
                if (interimDate2 == null) { return null; }
                int year2 = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                return getCorrectDate(strDate + "." + month + "." + year2, pattern);
            default:
                return null;
        }
    }

    static Date getEndDate(Date startDate, Date duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MINUTE, duration.getHours() * 60 + duration.getMinutes());
        return cal.getTime();
    }

    static Date recalculateEndDate(Date startOldDate, Date endOldDate, Date startNewDate) {
        int diffInMinutes = (int)((endOldDate.getTime() - startOldDate.getTime()) / (1000 * 60));
        Calendar cal = Calendar.getInstance();
        cal.setTime(startNewDate);
        cal.add(Calendar.MINUTE, diffInMinutes);
        return cal.getTime();
    }

    static boolean doNotesIntersect(Date firstStart, Date firstEnd, Date secondStart, Date secondEnd) {
        return !((firstStart.after(secondStart) && firstEnd.after(secondEnd) &&
                firstStart.after(secondEnd)) ||
                (firstStart.before(secondStart) && firstEnd.before(secondEnd) &&
                        secondStart.after(firstEnd)));
    }

    static String getCorrectStringFromDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    static boolean isConflict(Map<String, Log> tasks, Log newLog) {
        for (Log log : tasks.values()) {
            if (DateWorker.doNotesIntersect(log.startDate, log.endDate, newLog.startDate, newLog.endDate)){
                return true;
            }
        }
        return false;
    }
}
