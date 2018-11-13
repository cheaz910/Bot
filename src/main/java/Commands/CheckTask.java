package Commands;

import Data.Log;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckTask {
    public static String help() {
        return "Введите дату начала события, которое вы хотите отметить выполненым\n" +
                "Формат ввода: HH:mm-dd.MM.yyyy,\n" +
                "          или HH:mm-dd.MM,\n" +
                "          или HH:mm-dd";
    }

    public static void doCommand(String strDate,
                                 ConcurrentHashMap<String, Log> tasks,
                                 PrintStream outputStream) {
        Date date =  DateWorker.complementDate(strDate);
        if (date == null) {
            outputStream.println("Неверный формат даты: " + strDate);
            return;
        }
        String newStrDate = DateWorker.getCorrectStringFromDate(date, "HH:mm-dd.MM.yyyy");
        Log log = tasks.get(newStrDate);
        if (log == null) {
            outputStream.println("Такого события нет");
            return;
        }
        log.check = true;
        outputStream.println("Событие отмечено, как выполненное");
    }
}
