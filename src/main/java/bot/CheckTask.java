package bot;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;

public class CheckTask {
    static String help() {
        return "Введите дату начала события, которое вы хотите отметить выполненым\n" +
                "Формат ввода: HH:mm-dd.MM.yyyy,\n" +
                "          или HH:mm-dd.MM,\n" +
                "          или HH:mm-dd";
    }

    static void doCommand(String strDate, Map<String, Log> tasks, PrintStream outputStream) {
        Date date =  DateWorker.complementDate(strDate);
        if (date == null) {
            outputStream.println("Неверный формат даты: " + strDate);
            return;
        }
        String newStrDate = DateWorker.getCorrectStringFromDate(date, "HH:mm-dd.MM.yyyy");
        if (!tasks.containsKey(newStrDate)) {
            outputStream.println("Такого события нет");
            return;
        }
        tasks.get(newStrDate).check = true;
        outputStream.println("Событие отмечено, как выполненное");
    }
}
