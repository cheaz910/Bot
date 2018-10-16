package bot;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.Gson;

class Bot {
    private Scanner in;
    private PrintStream outputStream;
    Map<String, Map<String, Log>> logAllUsers;

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
        logAllUsers = ConvertToMap(FileWorker.ReadFile("Log.txt"));
        if (logAllUsers == null)
            logAllUsers = new HashMap<>();
    }

    void SaveInfo() {
        try {
            FileWorker.WriteFile("Log.txt", ConvertToJson(logAllUsers));
            outputStream.println("Информация успешно сохранена.");
        }
        catch (Exception e) {
            outputStream.println("Произошла ошибка при сохранении.");
        }
    }

    String GetGreeting(String name) {
        return String.format("Здравствуй, %s!", name);
    }

    String GetHelp() {
        return "Чтобы добавить событие, введите: +. \n" +
                "Чтобы удалить событие, введите: -. \n" +
                "Чтобы перенести событие, введите: перенести. \n" +
                "Чтобы посмотреть все события за день, введите: день. \n" +
                "Чтобы посмотреть все события за месяц, введите: месяц. \n" +
                "Чтобы сказать спасибо, введите: спасибо. \n" +
                "Чтобы сохранить календарь, введите: сохранить. ";
    }

    void AddNote(String[] info, Map<String, Log> notes) {
        if (info.length != 3){
            outputStream.println("Неверный формат ввода");
            return;
        }
        String strStartDate = info[1];
        Date startDate = GetCorrectDate(strStartDate, "HH:mm-dd.MM.yyyy");
        Date duration = GetCorrectDate(info[2], "HH:mm");
        if (startDate == null || duration == null) {
            return;
        }
        Date endDate = GetEndDate(startDate, duration);
        Log newLog = new Log(info[0], startDate, endDate);
        if (IsConflict(notes, newLog)) {
            outputStream.println("На это время уже запланировано событие");
            return;
        }
        notes.put(GetCorrectStringFromDate(startDate, "HH:mm-dd.MM.yyyy"), newLog);
        outputStream.println("Событие добавлено");
    }

    Date GetEndDate(Date startDate, Date duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MINUTE, duration.getHours() * 60 + duration.getMinutes());
        return cal.getTime();
    }

    private boolean IsConflict(Map<String, Log> notes, Log newLog) {
        for (Log log : notes.values()) {
            if (DoNotesIntersect(log, newLog)){
                return true;
            }
        }
        return false;
    }

    private boolean DoNotesIntersect(Log first, Log second) {
        return !((first.startDate.after(second.startDate) && first.endDate.after(second.endDate) &&
                first.startDate.after(second.endDate)) ||
                (first.startDate.before(second.startDate) && first.endDate.before(second.endDate) &&
                        second.startDate.after(first.endDate)));
    }

    void RemoveNote(String strDate, Map<String, Log> notes) {
        Date date = GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        if (date == null) {
            outputStream.println("Неверный формат ввода");
            return;
        }
        if (!notes.containsKey(strDate)) {
            outputStream.println("Такого события нет");
            return;
        }
        notes.remove(strDate);
        outputStream.println("Событие удалено");
    }

    void TransferNote(String[] info, Map<String, Log> notes) {
        if (info.length != 2){
            outputStream.println("Неверный формат ввода");
            return;
        }
        String strOldDate = info[0];
        String strNewDate = info[1];
        Date oldDate = GetCorrectDate(strOldDate, "HH:mm-dd.MM.yyyy");
        Date newDate = GetCorrectDate(strNewDate, "HH:mm-dd.MM.yyyy");
        if (oldDate == null || newDate == null) {
            return;
        }
        String note = notes.get(strOldDate).note;
        Date endDate = RecalculateEndDate(oldDate, notes.get(strOldDate).endDate, newDate);
        Log newLog = new Log(note, newDate, endDate);
        if (IsConflict(notes, newLog)) {
            outputStream.println("На это время уже запланировано событие");
            return;
        }
        notes.remove(strOldDate);
        notes.put(strNewDate, newLog);
        outputStream.println("Событие перенесено");
    }

    Date RecalculateEndDate(Date startOldDate, Date endOldDate, Date startNewDate) {
        int diffInMinutes = (int)((endOldDate.getTime() - startOldDate.getTime()) / (1000 * 60));
        Calendar cal = Calendar.getInstance();
        cal.setTime(startNewDate);
        cal.add(Calendar.MINUTE, diffInMinutes);
        return cal.getTime();
    }

    ArrayList<Log> GetNotes(String strMonthOrDay, Map<String, Log> log, String pattern) {
        Date needDate = GetCorrectDate(strMonthOrDay, pattern);
        if (needDate == null) {
            return new ArrayList<>();
        }
        ArrayList<Log> result = new ArrayList<>();
        for (String date : log.keySet()) {
            Date currentDate = log.get(date).startDate;
            if (needDate.getYear() == currentDate.getYear() &&
                    needDate.getMonth() == currentDate.getMonth() &&
                    (needDate.getDate() == currentDate.getDate() || pattern.equals("MM.yyyy"))) {
                result.add(log.get(date));
            }
        }
        return result;
    }

    void DisplayListOfNotes(ArrayList<Log> notes) {
        String pattern = "HH:mm-dd.MM.yyyy";
        for (Log log : notes) {
            outputStream.println("Начало события: " + GetCorrectStringFromDate(log.startDate, pattern) +
                    " Конец события: " + GetCorrectStringFromDate(log.endDate, pattern) +
                    " Cобытие: " + log.note);
        }
    }

    Date GetCorrectDate(String strDate, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date;
        try {
            date = format.parse(strDate);
        }
        catch (ParseException e) {
            outputStream.println("Неверный формат даты/времени. [" + strDate + "]");
            return null;
        }
        return date;
    }

    private String GetCorrectStringFromDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    String ConvertToJson(Map<String, Map<String, Log>> log) {
        return new Gson().toJson(log, HashMap.class);
    }

    HashMap<String, Map<String, Log>> ConvertToMap(String log) {
        return new Gson().fromJson(log, MyMap.class);
    }
}
