package bot;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RemoveTasks {
    static String help(String interval) {
        switch (interval) {
            case "":
                return "Введите дату начала события.\n" +
                        "Формат ввода: HH:mm-dd.MM.yyyy\n" +
                        "          или HH:mm-dd.MM\n" +
                        "          или HH:mm-dd";
            case "день":
                return getHelp("день", "dd.MM.yyyy");
            case "месяц":
                return getHelp("месяц", "MM.yyyy");
            case "год":
                return getHelp("год", "yyyy");
            default:
                return null;
        }
    }

    static private String getHelp(String interval, String pattern){
        return "Введите " + interval + ", события которого хотите удалить.\n" +
                "Формат ввода: " + pattern;
    }

    static void removeOneTask(String strDate, Map<String, Log> tasks, PrintStream outputStream) {
        Date date = DateWorker.complementDate(strDate);
        if (date == null) {
            outputStream.println("Неверный формат даты: " + strDate);
            return;
        }
        String newStrDate = DateWorker.getCorrectStringFromDate(date, "HH:mm-dd.MM.yyyy");
        if (!tasks.containsKey(newStrDate)) {
            outputStream.println("Такого события нет");
            return;
        }
        tasks.remove(newStrDate);
        outputStream.println("Событие удалено");
    }

    static void removeTasksOfDayMonthYear(String strDate, Map<String, Log> tasks, String pattern, PrintStream outputStream) {
        Date date = DateWorker.getCorrectDate(strDate, pattern);
        if (date == null) {
            outputStream.println("Неверный формат даты: " + strDate);
            return;
        }
        ArrayList<String> dates = new ArrayList<>();
        for (String dateOfNote : tasks.keySet()) {
            if (dateOfNote.endsWith(strDate) ){
                dates.add(dateOfNote);
            }
        }
        for (String dateOfNote : dates) {
            tasks.remove(dateOfNote);
        }
        outputStream.println("События удалены");
    }
}