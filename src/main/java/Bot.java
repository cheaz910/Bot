
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;


class Bot {
    private String NameOfUser = "";

    void Start(){
        NameOfUser = GetName();
        Map<String, Map<String, Log>> log = ConvertToMap(FileWorker.ReadFile("Log.txt"));
        if (log == null)
            log = new HashMap<>();
        if (!log.containsKey(NameOfUser))
            log.put(NameOfUser, new HashMap());
        System.out.println(GetGreeting(NameOfUser));
        String command = "";
        Scanner in = new Scanner(System.in);
        while (!"Пока".equals(command)) {
            command = in.nextLine();
            if (command.startsWith("Добавь событие ")) {
                AddNote(command.substring(15), log);
                System.out.println("Событие добавлено");
            }
            else if (command.startsWith("Покажи список событий за месяц ")) {
                ArrayList<String> result = GetNotes(command.substring(32),
                        log.get(NameOfUser),
                        "MM.yyyy");
                System.out.println(result);
            }
            else if (command.startsWith("Покажи список событий ")) {
                ArrayList<String> result = GetNotes(command.substring(22),
                                                    log.get(NameOfUser),
                                                    "dd.MM.yyyy");
                System.out.println(result);
            }
            else if (command.startsWith("Перенеси событие с ")) {
                TransferNote(command, log.get(NameOfUser));
                System.out.println("Событие перенесено");
            }
            else if (command.startsWith("Конфликты?")) {
                 ArrayList<String> conflicts = GetConflicts(log.get(NameOfUser));
                 System.out.println("Обнаруженнные конфликты:");
                 for (String conflict : conflicts) {
                     System.out.println(conflict);
                 }
            }
            else
                System.out.println("Неизвестная команда");
        }
        FileWorker.WriteFile("Log.txt", ConvertToJson(log));
    }

    private ArrayList<String> GetConflicts(Map<String, Log> log) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> stringDates = new ArrayList<>(log.keySet());
        Date[] dates = new Date[stringDates.size()];
        Date[] endsDates = new Date[stringDates.size()];
        SimpleDateFormat format = new SimpleDateFormat("HH:mm-dd.MM.yyyy");
        for (int i = 0; i < stringDates.size(); i++) {
            try {
                dates[i] = format.parse(stringDates.get(i));
                endsDates[i] = format.parse(log.get(stringDates.get(i)).endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < dates.length; i++) {
            for (int j = i + 1; j < dates.length; j++) {
                if (dates[i].before(dates[j]) && endsDates[i].after(dates[j]) ||
                    dates[i].after(dates[j]) && dates[i].before(endsDates[j]))
                    result.add(format.format(dates[i]) + "--" + format.format(endsDates[i]) +
                            " и " + format.format(dates[j]) + "--" + format.format(endsDates[j]));
            }
        }
        return result;
    }

    private void TransferNote(String command, Map<String, Log> log) {
        String[] info = command.split(" ");
        if (info.length != 2) {
            System.out.println("Неверный формат данных. Пример: 'Перенеси событие с Х на Y'");
            return;
        }
        String oldDate = info[0];
        String newDate = info[1];
        Log note = log.get(oldDate);
        log.remove(oldDate);
        if (log.containsKey(newDate))
            System.out.println("Данное время уже занято");
        else
            log.put(newDate, note);
    }

    // Получить все записи за месяц или за день(зависит от pattern)
    private ArrayList<String> GetNotes(String command,
                                       Map<String, Log> log,
                                       String pattern) {
        SimpleDateFormat formatWithTime = new SimpleDateFormat("HH:mm-dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        ArrayList<String> result = new ArrayList<>();
        try {
            Date needDate = format.parse(command);
            for (String date : log.keySet()) {
                Date currentDate = formatWithTime.parse(date);
                if (needDate.getYear() == currentDate.getYear() &&
                        needDate.getMonth() == currentDate.getMonth() &&
                        (needDate.getDay() == currentDate.getDay() || pattern.equals("MM.yyyy"))) {
                    result.add(date);
                }
            }
        } catch (ParseException e) {
            System.out.println("Неверный формат даты/времени. [" + pattern + "]");
        }
        return result;
    }

    private void AddNote(String command, Map<String, Map<String, Log>> log) {
        final String[] info = command.split(" "); // массив состоящий из: даты начала, даты конца и события
        Map<String, Log> notes = log.get(NameOfUser);
        if (notes != null) {
            if (notes.containsKey(info[0]))
                System.out.println("Данное время уже занято");
            else {
                String note = command.substring(info[0].length() + info[1].length() + 1);
                notes.put(info[0], new Log(info[0], info[1], note));
            }
        }
    }

    private String ConvertToJson(Map<String, Map<String, Log>> log) {
        return new Gson().toJson(log, HashMap.class);
    }

    private HashMap<String, Map<String, Log>> ConvertToMap(String log) {
        return new Gson().fromJson(log, MyMap.class);
    }

    private String GetName() {
        System.out.println("Как вас зовут?");
        String name;
        Scanner in = new Scanner(System.in);
        name = in.nextLine();
        return name;
    }

    private String GetGreeting(String name) {
        return String.format("Привет, %s!", name);
    }
}
