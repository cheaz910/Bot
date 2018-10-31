package Commands;

import Data.Log;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;

public class TransferTask {
    public static String help() {
        return "Введите сначала дату, с которой нужно перенести, затем дату, на которую нужно перенести.\n" +
                "Формат ввода дат: HH:mm-dd.MM.yyyy, HH:mm-dd.MM HH:mm-dd.MM, HH:mm-dd";
    }

    public static void doCommand(String dates, Map<String, Log> tasks, PrintStream outputStream) {
        String[] info = dates.split(" ");
        if (info.length != 2){
            outputStream.println("Неверный формат ввода");
            return;
        }
        String strOldDate = info[0];
        String strNewDate = info[1];
        Date oldDate = DateWorker.complementDate(strOldDate);
        if (oldDate == null) {
            outputStream.println("Неверный формат даты: " + strOldDate);
            return;
        }
        strOldDate = DateWorker.getCorrectStringFromDate(oldDate, "HH:mm-dd.MM.yyyy");
        Date newDate = DateWorker.complementDate(strNewDate);
        if (newDate == null) {
            outputStream.println("Неверный формат даты: " + strNewDate);
            return;
        }
        strNewDate = DateWorker.getCorrectStringFromDate(newDate, "HH:mm-dd.MM.yyyy");
        String task = tasks.get(strOldDate).task;
        boolean check = tasks.get(strOldDate).check;
        Date endDate = DateWorker.recalculateEndDate(oldDate, tasks.get(strOldDate).endDate, newDate);
        Log newLog = new Log(task, newDate, endDate, check);
        if (DateWorker.isConflict(tasks, newLog)) {
            outputStream.println("На это время уже запланировано событие");
            return;
        }
        tasks.remove(strOldDate);
        tasks.put(strNewDate, newLog);
        outputStream.println("Событие перенесено");
    }
}
