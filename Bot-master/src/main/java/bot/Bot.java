package bot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.Gson;

class Bot {
    private String NameOfUser = "";

    void Start() {
        NameOfUser = GetName();

        Map<String, Map<String, Log>> log = ConvertToMap(FileWorker.ReadFile("Log.txt"));
        if (log == null)
            log = new HashMap<>();
        if (!log.containsKey(NameOfUser))
            log.put(NameOfUser, new HashMap());

        System.out.println(GetGreeting(NameOfUser));
        System.out.println(GetHelp());

        Scanner in = new Scanner(System.in);
        boolean isCommand = true;
        while (isCommand) {
            switch(in.nextLine()) {
                case "+":
                    System.out.println("Введите через пробел событие, дату начала и продолжительность события. " +
                            "Формат ввода: событие HH:mm-dd.MM.yyyy HH:mm");
                    AddNote(in.nextLine().split(" "), log.get(NameOfUser));
                    break;
                case "-":
                    System.out.println("Введите дату начала события. " +
                            "Формат ввода: HH:mm-dd.MM.yyyy");
                    RemoveNote(in.nextLine(), log.get(NameOfUser));
                    break;
                case "перенести":
                    System.out.println("Введите сначала дату, с которой нужно перенести, " +
                            "затем дату, на которую нужно перенести. " +
                            "Формат ввода: HH:mm-dd.MM.yyyy HH:mm-dd.MM.yyyy");
                    TransferNote(in.nextLine().split(" "), log.get(NameOfUser));
                    break;
                case "день":
                    System.out.println("Ведите интересующий вас день. " +
                            "Формат ввода: dd.MM.yyyy");
                    ArrayList<Log> notesForDay = GetNotes(in.nextLine(), log.get(NameOfUser), "dd.MM.yyyy");
                    DisplayListOfNotes(notesForDay, "HH:mm-dd.MM.yyyy");
                    break;
                case "месяц":
                    System.out.println("Введите интересующий вас месяц. " +
                            "Формат ввода: MM.yyyy");
                    ArrayList<Log> notesForMonth = GetNotes(in.nextLine(), log.get(NameOfUser), "MM.yyyy");
                    DisplayListOfNotes(notesForMonth, "HH:mm-dd.MM.yyyy");
                    break;
                case "спасибо":
                    isCommand = false;
                    break;
                default:
                    System.out.println("Неизвестная команда");
                    GetHelp();
                    break;
            }
        }
        FileWorker.WriteFile("Log.txt", ConvertToJson(log));
    }

    private String GetName() {
        System.out.println("Как вас зовут?");
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private String GetGreeting(String name) {
        return String.format("Здравствуй, %s!", name);
    }

    private String GetHelp() { //добавить перенос строки
        return "Чтобы добавить событие, введите: +. " +
                "Чтобы удалить событие, введите: -. " +
                "Чтобы перенести событие, введите: перенести. " +
                "Чтобы посмотреть все события за день, введите: день. " +
                "Чтобы посмотреть все события за месяц, введите: месяц. " +
                "Чтобы завершить работу, введите: спасибо. ";
    }

    private void AddNote(String[] info, Map<String, Log> notes) {
        if (info.length != 3){
            System.out.println("Неверный формат ввода");
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
        //if (IsConflict(notes, newLog)) {             если это раскомментить оно сломается
        //    System.out.println("На это время уже запланировано событие");
        //    return;
        //}
        notes.put(strStartDate, newLog);
        System.out.println("Событие добавлено");
    }

    private Date GetEndDate(Date startDate, Date duration) {
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
        return !((first.startDate.after(second.startDate) && first.endDate.after(second.endDate)) ||
                (first.startDate.before(second.startDate) && first.endDate.before(second.endDate)));
    }

    private void RemoveNote(String strDate, Map<String, Log> notes) {
        Date date = GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        if (date == null) {
            return;
        }
        notes.remove(strDate);
        System.out.println("Событие удалено");
    }

    private void TransferNote(String[] info, Map<String, Log> notes) {
        if (info.length != 2){
            System.out.println("Неверный формат ввода");
            return;
        }
        String strOldDate = info[0];
        String strNewDate = info[1];
        Date oldDate = GetCorrectDate(strOldDate, "HH:mm-dd.MM.yyyy");
        Date newDate = GetCorrectDate(strNewDate, "HH:mm-dd.MM.yyyy");
        if (oldDate == null || newDate == null) {
            return;
        }
        // пересчёт конечной даты
        Log note = notes.get(strOldDate);
        notes.remove(strOldDate);
        notes.put(strNewDate, note);
        System.out.println("Событие перенесено");
    }


    private ArrayList<Log> GetNotes(String strMonthOrDay, Map<String, Log> log, String pattern) {
        Date needDate = GetCorrectDate(strMonthOrDay, pattern);
        if (needDate == null) {
            return null;
        }
        ArrayList<Log> result = new ArrayList<>();
        for (String date : log.keySet()) {
            Date currentDate = log.get(date).startDate;
            if (needDate.getYear() == currentDate.getYear() &&
                    needDate.getMonth() == currentDate.getMonth() &&
                    (needDate.getDay() == currentDate.getDay() || pattern.equals("MM.yyyy"))) {
                result.add(log.get(date));
            }
        }
        return result;
    }

    private void DisplayListOfNotes(ArrayList<Log> notes, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        for (Log log : notes) {
            System.out.println("Начало события: " + format.format(log.startDate) +
                    " Конец события: " + format.format(log.endDate) +
                    " Cобытие: " + log.note);
        }
    }

    private Date GetCorrectDate(String strDate, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date;
        try {
            date = format.parse(strDate);
        }
        catch (ParseException e) {
            System.out.println("Неверный формат даты/времени. [" + strDate + "]");
            return null;
        }
        return date;
    }

    private String ConvertToJson(Map<String, Map<String, Log>> log) {
        return new Gson().toJson(log, HashMap.class);
    }

    private HashMap<String, Map<String, Log>> ConvertToMap(String log) {
        return new Gson().fromJson(log, MyMap.class);
    }
}
