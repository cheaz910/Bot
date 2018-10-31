package Commands;

import Data.Log;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;

public class AddTask {
    public static String help() {
        return "Введите через пробел событие, дату начала и продолжительность события.\n" +
                "Формат ввода: событие HH:mm-dd.MM.yyyy HH:mm,\n" +
                "          или событие HH:mm-dd.MM HH:mm,\n" +
                "          или событие HH:mm-dd HH:mm";
    }

    public static void doCommand(String task, Map<String, Log> tasks, PrintStream outputStream) {
        String[] info = task.split(" ");
        if (info.length < 3){
            outputStream.println("Неверный формат ввода: " + task);
            return;
        }
        String strStartDate = info[info.length-2];
        Date startDate = DateWorker.complementDate(strStartDate);
        if (startDate == null) {
            outputStream.println("Неверный формат даты: " + strStartDate);
            return;
        }
        String strDuration = info[info.length-1];
        Date duration = DateWorker.getCorrectDate(strDuration, "HH:mm");
        if (duration == null) {
            outputStream.println("Неверный формат продолжительности: " + strDuration);
            return;
        }
        Date endDate = DateWorker.getEndDate(startDate, duration);
        Log newLog = new Log(task.substring(0, task.length()-strStartDate.length()-strDuration.length()-2), startDate, endDate);
        if (DateWorker.isConflict(tasks, newLog)) {
            outputStream.println("На это время уже запланировано событие");
            return;
        }
        tasks.put(DateWorker.getCorrectStringFromDate(startDate, "HH:mm-dd.MM.yyyy"), newLog);
        outputStream.println("Событие добавлено");
    }
}
