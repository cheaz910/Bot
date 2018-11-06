package bot;

import java.io.PrintStream;
import java.util.*;

import FileWorker.FileWorker;
import Data.Log;
import Data.MyMap;
import com.google.gson.Gson;

class Bot {
    private PrintStream outputStream;
    private final static String filename = "Log.txt";
    Map<String, Map<String, Log>> logAllUsers = convertToMap(FileWorker.ReadFile(filename));

    Bot(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    Bot() {}

    Map<String, Log> getLogForUser(String nameOfUser, Map<String, Map<String, Log>> log) {
        if (!log.containsKey(nameOfUser))
            log.put(nameOfUser, new HashMap());
        return log.get(nameOfUser);
    }

    String getHelp() {
        return  "Чтобы добавить событие, введите: +. \n" +
                "Чтобы удалить событие, введите: -. \n" +
                "Чтобы удалить все события за день, введите: -день. \n" +
                "Чтобы удалить все события за месяц, введите: -месяц. \n" +
                "Чтобы удалить все события за год, введите: -год. \n" +
                "Чтобы перенести событие, введите: перенести. \n" +
                "Чтобы отметить выполненное событие, введите: выполнено. \n" +
                "Чтобы посмотреть все события за день, введите: день. \n" +
                "Чтобы посмотреть все события за месяц, введите: месяц. \n" +
                "Чтобы посмотреть праздники за день, введите: праздники. \n" +
                "Чтобы завершить работу, введите: спасибо. \n" +
                "Чтобы сохранить календарь, введите: сохранить. ";
    }

    void saveInfo() {
        try {
            FileWorker.WriteFile(filename, convertToJson(logAllUsers));
            outputStream.println("Информация успешно сохранена.");
        }
        catch (Exception e) {
            outputStream.println("Произошла ошибка при сохранении.");
        }
    }

    String convertToJson(Map<String, Map<String, Log>> log) {
        return new Gson().toJson(log, HashMap.class);
    }

    HashMap<String, Map<String, Log>> convertToMap(String log) {
        HashMap<String, Map<String, Log>> result = new Gson().fromJson(log, MyMap.class);
        return (result == null) ? new MyMap() : result;
    }
}
