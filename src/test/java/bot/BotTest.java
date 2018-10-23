/*package bot;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class BotTest {
    @Test
    public final void testGetLogForUser_NewUser() { // Новый пользователь, которого еще нет в логе
        Bot bot = new Bot();
        Map<String, Map<String, Log>> logAllUsers = new HashMap<>();
        assertEquals(0, logAllUsers.size());
        Map<String, Log> log = bot.GetLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(0, log.size());
    }

    @Test
    public final void testGetLogForUser_OldUser() { // Пользователь, который уже пользовался ботом
        Bot bot = new Bot();
        Map<String, Map<String, Log>> logAllUsers = new HashMap<>();
        logAllUsers.put("Java", new HashMap());
        logAllUsers.get("Java").put("<Date>", new Log("<note>", new Date(2018), new Date(2018)));
        assertEquals(1, logAllUsers.size());
        Map<String, Log> log = bot.GetLogForUser("Java", logAllUsers);
        assertEquals(1, logAllUsers.size());
        assertEquals(1, log.size());
    }



    @Test
    public final void testGetEndDate_LowDuration() { // Продолжительность до 24 часов
        Bot bot = new Bot();
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,2,30);
        Date endDate = DateWorker.GetEndDate(startDate, duration);
        assertEquals(16, endDate.getHours());
        assertEquals(10, endDate.getMinutes());
    }

    @Test
    public final void testGetEndDate_HighDuration() { // Продолжительность больше 24 часов
        Bot bot = new Bot();
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,25,30);
        Date endDate = DateWorker.GetEndDate(startDate, duration);
        assertEquals(15, endDate.getHours());
        assertEquals(10, endDate.getMinutes());
    }

    @Test
    public final void testRemoveNote_Default() { // Удаление существующего события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        notes.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        bot.RemoveNote("13:40-02.09.2018", notes);
        assertEquals("Событие удалено\n", outContent.toString());
        assertEquals(0, notes.size());
    }

    @Test
    public final void testRemoveNoteOfDayMonthYear_Default() { // Удаление событий за день, месяцб год
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        notes.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        notes.put("20:40-02.09.2018", new Log("событие2", new Date(0), new Date(0)));
        bot.RemoveNotesOfDayMonthYear("02.09.2018", "dd.MM.yyyy", notes);
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(0, notes.size());

        notes.put("13:40-02.10.2018", new Log("событие", new Date(0), new Date(0)));
        notes.put("20:40-02.09.2018", new Log("событие2", new Date(0), new Date(0)));
        bot.RemoveNotesOfDayMonthYear("09.2018", "MM.yyyy", notes);
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(1, notes.size());

        notes.put("13:40-02.09.2019", new Log("событие2", new Date(0), new Date(0)));
        bot.RemoveNotesOfDayMonthYear("2018", "yyyy", notes);
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(1, notes.size());
    }

    @Test
    public final void testRemoveNote_NotExist() { // Удаление не существующего события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        notes.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        bot.RemoveNote("13:45-02.09.2018", notes);
        assertEquals("Такого события нет\n", outContent.toString());
        assertEquals(1, notes.size());
    }

    @Test
    public final void testRemoveNote_WrongFormat() { // Неправильный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        notes.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        bot.RemoveNote("13:45123--1231202.09.2018", notes);
        assertEquals("Такого события нет\n", outContent.toString());
        assertEquals(1, notes.size());
    }

    @Test
    public final void testRecalculateEndDate_Default() { // Стандартные входные данные
        Bot bot = new Bot();
        Date startOldDate = new Date(2018, 9,2,13,40);
        Date endOldDate = new Date(2018, 9, 2, 15,45);
        Date startDate = new Date(2019,10,3,15,45);
        Date endDate = DateWorker.RecalculateEndDate(startOldDate, endOldDate, startDate);
        assertEquals(2019, endDate.getYear());
        assertEquals(10, endDate.getMonth());
        assertEquals(3, endDate.getDate());
        assertEquals(17, endDate.getHours());
        assertEquals(50, endDate.getMinutes());
    }

    @Test
    public final void testGetNotes_Default() { // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 15:30-02.09.2018 01:40";
        bot.AddNote(command, notes);
        String secondCommand = "событие 10:30-02.11.2018 01:40";
        bot.AddNote(secondCommand, notes);
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
        ArrayList<Log> listForMonth = bot.GetNotes("11.2018", notes, "MM.yyyy");
        assertEquals(1, listForMonth.size());
        ArrayList<Log> listForDay = bot.GetNotes("02.09.2018", notes, "dd.MM.yyyy");
        assertEquals(1, listForDay.size());
    }

    @Test
    public final void testGetNotes_WrongPattern() { // Передан день, но шаблон по месяцу
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 15:30-02.09.2018 01:40";
        bot.AddNote(command, notes);
        String secondCommand = "событие 10:30-02.11.2018 01:40";
        bot.AddNote(secondCommand, notes);
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
        ArrayList<Log> listForMonth = bot.GetNotes("02.11.2018", notes, "MM.yyyy");
        assertEquals(0, listForMonth.size());
    }

    @Test
    public final void testGetCorrectDate_Default() { // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        String strDate = "15:30-02.09.2018";
        Date date = DateWorker.GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        assertEquals(new Date(118, 8, 2, 15, 30), date);
        assertEquals("", outContent.toString());
    }

    @Test
    public final void testGetCorrectDate_WrongFormat() { // Неверный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        String strDate = "15:30----юю02.09.2018";
        Date date = DateWorker.GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        assertEquals("Неверный формат даты/времени. [15:30----юю02.09.2018]\n", outContent.toString());
        assertNull(date);
    }

    @Test
    public final void testJsonConverts() { // Проверка перевода из json в map и наоборот
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Map<String, Log>> notes = new HashMap<>();
        notes.put("Ilnur", new HashMap<String, Log>());
        String command = "событие 15:30-02.09.2018 01:40";
        bot.AddNote(command, notes.get("Ilnur"));
        assertEquals("Событие добавлено\n", outContent.toString());
        String json = bot.ConvertToJson(notes);
        Map<String, Map<String, Log>> secondNotes = bot.ConvertToMap(json);
        assertEquals(notes.get("Ilnur").keySet(), secondNotes.get("Ilnur").keySet());
        Log log = notes.get("Ilnur").get("15:30-02.09.2018");
        Log secondLog = secondNotes.get("Ilnur").get("15:30-02.09.2018");
        assertEquals(log.startDate, secondLog.startDate);
    }
}*/