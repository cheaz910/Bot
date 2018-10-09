package bot;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public final void testGetGreeting() {
        Bot bot = new Bot();
        assertThat(bot.GetGreeting("Ильнур"), is("Здравствуй, Ильнур!"));
    }

    @Test
    public final void testAddNote_Default() {   // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 12:10";
        bot.AddNote(command.split(" "), notes);
        assertTrue(notes.containsKey("13:40-02.09.2018"));
        Log log = notes.get("13:40-02.09.2018");
        assertEquals(log.note, "событие");
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals("Событие добавлено\n", outContent.toString());
        assertEquals(bot.GetCorrectDate("13:40-02.09.2018", pattern), log.startDate);
        assertEquals(bot.GetCorrectDate("01:50-03.09.2018", pattern), log.endDate);
    }

    private boolean WrongFormatAddNote(String command, ByteArrayOutputStream outContent) {
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        bot.AddNote(command.split(" "), notes);
        return notes.size() == 0;
    }

    @Test
    public final void testAddNote_WrongFormat() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertTrue(WrongFormatAddNote("событие 13:40-02.09.2018", outContent)); // На входе не указывается продолжительность
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatAddNote("событие", outContent)); // На входе не указывается начало и продолжительность
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatAddNote("", outContent)); // На входе пустая строка
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatAddNote("событие 11340-02.09.2018 01:30", outContent)); // На входе начало в неправильном формате
        assertEquals("Неверный формат даты/времени. [11340-02.09.2018]\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatAddNote("событие 13:40-02.09.2018 11330", outContent)); // На входе продолжительность в неправильном формате
        assertEquals("Неверный формат даты/времени. [11330]\n", outContent.toString());
    }

    @Test
    public final void testAddNote_BadFormat() { // На входе часы больше 24 (лишние часы должны перейти в дни)
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 25:40-02.09.2018 01:10";
        bot.AddNote(command.split(" "), notes);
        assertTrue(notes.containsKey("01:40-03.09.2018"));
        Log log = notes.get("01:40-03.09.2018");
        assertEquals(log.note, "событие");
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals("Событие добавлено\n", outContent.toString());
        assertEquals(bot.GetCorrectDate("01:40-03.09.2018", pattern), log.startDate);
        assertEquals(bot.GetCorrectDate("02:50-03.09.2018", pattern), log.endDate);
    }

    private Map<String, Log> GetNotesAfterAdding(String secondCommand, ByteArrayOutputStream outContent) {
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 01:10";
        bot.AddNote(command.split(" "), notes);
        bot.AddNote(secondCommand.split(" "), notes);
        return notes;
    }

    @Test
    public final void testIsConflict_NoIntersection() { // Два события не пересекаются
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(2, GetNotesAfterAdding("событие2 12:10-02.09.2018 1:10", outContent).size());
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
    }


    @Test
    public final void testIsConflict_OneIntersection() { // События пересекаются в одной точке
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, GetNotesAfterAdding("событие2 12:10-02.09.2018 1:30", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }


    @Test
    public final void testIsConflict_Intersection1() { // Конец одного события находится в промежутке другого события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, GetNotesAfterAdding("событие2 12:10-02.09.2018 01:35", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }

    @Test
    public final void testIsConflict_Nesting() { // Одно событие находится в промежутке другого события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, GetNotesAfterAdding("событие2 13:45-02.09.2018 00:30", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }

    @Test
    public final void testGetEndDate_LowDuration() { // Продолжительность до 24 часов
        Bot bot = new Bot();
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,2,30);
        Date endDate = bot.GetEndDate(startDate, duration);
        assertEquals(16, endDate.getHours());
        assertEquals(10, endDate.getMinutes());
    }

    @Test
    public final void testGetEndDate_HighDuration() { // Продолжительность больше 24 часов
        Bot bot = new Bot();
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,25,30);
        Date endDate = bot.GetEndDate(startDate, duration);
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
    public final void testTransferNote_Default() {   // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 12:10";
        bot.AddNote(command.split(" "), notes);
        assertEquals("Событие добавлено\n", outContent.toString());
        String note = notes.get("13:40-02.09.2018").note;
        String secondCommand = "13:40-02.09.2018 15:25-03.10.2019";
        outContent.reset();
        bot.TransferNote(secondCommand.split(" "), notes);
        assertEquals("Событие перенесено\n", outContent.toString());
        assertTrue(notes.containsKey("15:25-03.10.2019"));
        assertEquals(1, notes.size());
        Log log = notes.get("15:25-03.10.2019");
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals(bot.GetCorrectDate("15:25-03.10.2019", pattern), log.startDate);
        assertEquals(bot.GetCorrectDate("03:35-04.10.2019", pattern), log.endDate);
        assertEquals(note, log.note);
    }

    private boolean WrongFormatTransferNote(String secondCommand, ByteArrayOutputStream outContent) {
        Bot bot = new Bot(new PrintStream(outContent));
        Map<String, Log> notes = new HashMap<>();
        String command = "событие 13:40-02.09.2018 12:10";
        bot.AddNote(command.split(" "), notes);
        assertEquals("Событие добавлено\n", outContent.toString());
        outContent.reset();
        bot.TransferNote(secondCommand.split(" "), notes);
        return notes.containsKey("13:40-02.09.2018");
    }

    @Test
    public final void testTransferNote_WrongFormat() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertTrue(WrongFormatTransferNote("13:40-02.09.2018", outContent));
        // На входе не указывается куда перенести
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("", outContent));
        // На входе пустая строка
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("11340---...-02.09.2018 15:40-03.09.2018", outContent));
        // Первая дата с ошибкой
        assertEquals("Неверный формат даты/времени. [11340---...-02.09.2018]\n", outContent.toString());
        outContent.reset();
        assertTrue(WrongFormatTransferNote("13:40-02.09.2018 11330--..32140.1", outContent));
        // Вторая дата с ошибкой
        assertEquals("Неверный формат даты/времени. [11330--..32140.1]\n", outContent.toString());
    }

    @Test
    public final void testRecalculateEndDate_Default() { // Стандартные входные данные
        Bot bot = new Bot();
        Date startOldDate = new Date(2018, 9,2,13,40);
        Date endOldDate = new Date(2018, 9, 2, 15,45);
        Date startDate = new Date(2019,10,3,15,45);
        Date endDate = bot.RecalculateEndDate(startOldDate, endOldDate, startDate);
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
        bot.AddNote(command.split(" "), notes);
        String secondCommand = "событие 10:30-02.11.2018 01:40";
        bot.AddNote(secondCommand.split(" "), notes);
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
        bot.AddNote(command.split(" "), notes);
        String secondCommand = "событие 10:30-02.11.2018 01:40";
        bot.AddNote(secondCommand.split(" "), notes);
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
        ArrayList<Log> listForMonth = bot.GetNotes("02.11.2018", notes, "MM.yyyy");
        assertEquals(0, listForMonth.size());
    }

    @Test
    public final void testGetCorrectDate_Default() { // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        String strDate = "15:30-02.09.2018";
        Date date = bot.GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        assertEquals(new Date(118, 8, 2, 15, 30), date);
        assertEquals("", outContent.toString());
    }

    @Test
    public final void testGetCorrectDate_WrongFormat() { // Неверный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Bot bot = new Bot(new PrintStream(outContent));
        String strDate = "15:30----юю02.09.2018";
        Date date = bot.GetCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
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
        bot.AddNote(command.split(" "), notes.get("Ilnur"));
        assertEquals("Событие добавлено\n", outContent.toString());
        String json = bot.ConvertToJson(notes);
        Map<String, Map<String, Log>> secondNotes = bot.ConvertToMap(json);
        assertEquals(notes.get("Ilnur").keySet(), secondNotes.get("Ilnur").keySet());
        Log log = notes.get("Ilnur").get("15:30-02.09.2018");
        Log secondLog = secondNotes.get("Ilnur").get("15:30-02.09.2018");
        assertEquals(log.startDate, secondLog.startDate);
    }
}