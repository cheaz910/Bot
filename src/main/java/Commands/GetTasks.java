package Commands;

import Data.Log;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GetTasks {
    public static String help(String interval) {
        switch (interval) {
            case "день":
                return getHelp("день", "dd.MM.yyyy");
            case "месяц":
                return getHelp("месяц", "MM.yyyy");
            default:
                return null;
        }
    }

    static private String getHelp(String interval, String pattern){
        return "Введите интересующий вас " + interval + ".\n" +
                "Формат ввода: " + pattern;
    }

    public static void doCommand(String strDate,
                                 ConcurrentHashMap<String, Log> log,
                                 String pattern,
                                 PrintStream outputStream){
        ArrayList<Log> notesForDay = getTasks(strDate, log, pattern, outputStream);
        displayListOfNotes(notesForDay, outputStream);
    }

    public static ArrayList<Log> getTasks(String strMonthOrDay,
                                          ConcurrentHashMap<String, Log> log,
                                          String pattern,
                                          PrintStream outputStream) {
        Date needDate = DateWorker.getCorrectDate(strMonthOrDay, pattern);
        if (needDate == null) {
            outputStream.println("Неверный формат даты: " + strMonthOrDay);
            return new ArrayList<>();
        }
        ArrayList<Log> result = new ArrayList<>();
        for (Map.Entry<String, Log> entry : log.entrySet()) {
            Date currentDate = entry.getValue().startDate;
            if (needDate.getYear() == currentDate.getYear() &&
                    needDate.getMonth() == currentDate.getMonth() &&
                    (needDate.getDate() == currentDate.getDate() || pattern.equals("MM.yyyy"))) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    static private void displayListOfNotes(ArrayList<Log> notes, PrintStream outputStream) {
        String pattern = "HH:mm-dd.MM.yyyy";
        for (Log log : notes) {
            String isDone = log.check ? "Да" : "Нет";
            outputStream.println("Cобытие: " + log.task + " Начало события: " +
                    DateWorker.getCorrectStringFromDate(log.startDate, pattern) +
                    " Конец события: " + DateWorker.getCorrectStringFromDate(log.endDate, pattern)
                    + " Выполнено: " + isDone);
        }
    }
}
