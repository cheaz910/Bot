package bot;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import com.google.gson.Gson;

class Bot {
    private Scanner in;
    private PrintStream outputStream;

    Bot(PrintStream outputStream, InputStream inputStream) {
        this.outputStream = outputStream;
        in = new Scanner(inputStream);
    }

    Bot(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    Bot() {}

    Map<String, Log> GetLogForUser(String nameOfUser, Map<String, Map<String, Log>> log) {
        if (!log.containsKey(nameOfUser))
            log.put(nameOfUser, new HashMap());
        return log.get(nameOfUser);
    }

    void Start() {
        outputStream.println("Как вас зовут?");
        String nameOfUser = in.nextLine();

        Map<String, Map<String, Log>> logAllUsers = ConvertToMap(FileWorker.ReadFile("Log.txt"));
        if (logAllUsers == null)
            logAllUsers = new HashMap<>();

        Map<String, Log> log = GetLogForUser(nameOfUser, logAllUsers);

        outputStream.println(String.format("Здравствуй, %s!", nameOfUser));
        outputStream.println(GetHelp());

        boolean isCommand = true;
        while (isCommand) {
            switch(in.nextLine()) {
                case "+":
                    outputStream.println(AddTask.help());
                    AddTask.doCommand(in.nextLine(), log, outputStream);
                    break;
                case "-":
                    outputStream.println(RemoveTasks.help(""));
                    RemoveTasks.removeOneTask(in.nextLine(), log, outputStream);
                    break;
                case "-день":
                    outputStream.println(RemoveTasks.help("день"));
                    RemoveTasks.removeTasksOfDayMonthYear(in.nextLine(), log, "dd.MM.yyyy", outputStream);
                    break;
                case "-месяц":
                    outputStream.println(RemoveTasks.help("месяц"));
                    RemoveTasks.removeTasksOfDayMonthYear(in.nextLine(), log, "MM.yyyy", outputStream);
                    break;
                case "-год":
                    outputStream.println(RemoveTasks.help("год"));
                    RemoveTasks.removeTasksOfDayMonthYear(in.nextLine(), log, "yyyy", outputStream);
                    break;
                case "перенести":
                    outputStream.println(TransferTask.help());
                    TransferTask.doCommand(in.nextLine(), log, outputStream);
                    break;
                case "выполнено":
                    outputStream.println( CheckTask.help());
                    CheckTask.doCommand(in.nextLine(), log, outputStream);
                    break;
                case "день":
                    outputStream.println(GetTasks.help("день"));
                    GetTasks.doCommand(in.nextLine(), log, "dd.MM.yyyy", outputStream);
                    break;
                case "месяц":
                    outputStream.println(GetTasks.help("месяц"));
                    GetTasks.doCommand(in.nextLine(), log, "MM.yyyy", outputStream);
                    break;
                case "спасибо":
                    isCommand = false;
                    break;
                case "справка":
                    outputStream.println(GetHelp());
                    break;
                default:
                    outputStream.println("Неизвестная команда");
                    break;
            }
        }
        FileWorker.WriteFile("Log.txt", ConvertToJson(logAllUsers));
    }

    String GetHelp() {
        return  "Чтобы добавить событие, введите: +. \n" +
                "Чтобы удалить событие, введите: -. \n" +
                "Чтобы удалить все события за день, введите: -день. \n" +
                "Чтобы удалить все события за месяц, введите: -месяц. \n" +
                "Чтобы удалить все события за год, введите: -год. \n" +
                "Чтобы перенести событие, введите: перенести. \n" +
                "Чтобы отметить посещённое событие, введите: выполнено. \n" +
                "Чтобы посмотреть все события за день, введите: день. \n" +
                "Чтобы посмотреть все события за месяц, введите: месяц. \n" +
                "Чтобы завершить работу, введите: спасибо. \n" +
                "Чтобы сохранить календарь, введите: сохранить. ";
    }

    String ConvertToJson(Map<String, Map<String, Log>> log) {
        return new Gson().toJson(log, HashMap.class);
    }

    HashMap<String, Map<String, Log>> ConvertToMap(String log) {
        return new Gson().fromJson(log, MyMap.class);
    }
}
