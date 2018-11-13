package Commands;

import Data.Log;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTaskGroup {
    public static String help() {
        return "Введите через пробел событие, дату начала, продолжительность события и список людей\n" +
                "Формат ввода: событие HH:mm-dd.MM.yyyy HH:mm user1 user2...,\n" +
                "          или событие HH:mm-dd.MM HH:mm user1 user2...,\n" +
                "          или событие HH:mm-dd HH:mm user1 user2...";
    }

    public static void doCommand(String command,
                                 String chatId,
                                 ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> logAllUsers,
                                 PrintStream outputStream) {
        String[] taskAndUsers = GetTaskAndUsers(command);
        if (taskAndUsers == null){
            outputStream.println("Неверный формат ввода: " + command);
            return;
        }
        String task = taskAndUsers[0];
        AddTask.doCommand(task, logAllUsers.get(chatId), outputStream);
        String strUsers = taskAndUsers[1];
        String[] users = strUsers.split(" ");
        for (String user : users) {
            AddTask.doCommand(task, logAllUsers.get(user), outputStream); // сообщения о добавлении будут отправляться одному пользователю
        }
    }

    static String[] GetTaskAndUsers(String command) {
        Pattern pattern = Pattern.compile("(.+:\\d\\d) (.+)");
        Matcher matcher = pattern.matcher(command);
        String[] result = new String[2];
        if (!matcher.find()) {
            return null;
        }
        else{
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
            return result;
        }
    }
}
