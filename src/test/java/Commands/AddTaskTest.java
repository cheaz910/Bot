package Commands;

import Commands.AddTask;
import Commands.DateWorker;
import Data.Log;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddTaskTest {
    @Test
    public final void testDefault() {   // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие 13:40-02.09.2018 12:10", tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("13:40-02.09.2018"));
        Log log = tasks.get("13:40-02.09.2018");
        assertEquals("событие", log.task);
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals(DateWorker.getCorrectDate("13:40-02.09.2018", pattern), log.startDate);
        assertEquals(DateWorker.getCorrectDate("01:50-03.09.2018", pattern), log.endDate);
        assertEquals(false, log.check);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testInputWithoutYear() {   // Входные данные без года
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие 13:40-02.09 12:10", tasks, new PrintStream(outContent));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String strDate = "13:40-02.09." + year;
        assertTrue(tasks.containsKey(strDate));
        Log log = tasks.get(strDate);
        assertEquals("событие", log.task);
        assertEquals(DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy"), log.startDate);
        Date startDate = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        Date duration = DateWorker.getCorrectDate("12:10", "HH:mm");
        assertEquals(DateWorker.getEndDate(startDate, duration), log.endDate);
        assertEquals(false, log.check);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testInputWithoutYearAndMonth() {   // Входные данные без года и месяца
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие 13:40-02 12:10", tasks, new PrintStream(outContent));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String strDate = "13:40-02." + month + "." + year;
        assertTrue(tasks.containsKey(strDate));
        Log log = tasks.get(strDate);
        assertEquals("событие", log.task);
        assertEquals(DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy"), log.startDate);
        Date startDate = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        Date duration = DateWorker.getCorrectDate("12:10", "HH:mm");
        assertEquals(DateWorker.getEndDate(startDate, duration), log.endDate);
        assertEquals(false, log.check);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testInputWithFewWords() {   // Входные данные с задачей, состоящей из более одного слова
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие1 событие2 13:40-02.09.2018 12:10", tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("13:40-02.09.2018"));
        Log log = tasks.get("13:40-02.09.2018");
        assertEquals("событие1 событие2", log.task);
        assertEquals(DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy"), log.startDate);
        assertEquals(DateWorker.getCorrectDate("01:50-03.09.2018", "HH:mm-dd.MM.yyyy"), log.endDate);
        assertEquals(false, log.check);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testСonflictingTasks() {   // конфликтующие события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие1 13:40-02.09.2018 12:10", tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("13:40-02.09.2018"));
        Log log = tasks.get("13:40-02.09.2018");
        assertEquals("событие1", log.task);
        assertEquals(DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy"), log.startDate);
        assertEquals(DateWorker.getCorrectDate("01:50-03.09.2018", "HH:mm-dd.MM.yyyy"), log.endDate);
        assertEquals(false, log.check);
        AddTask.doCommand("событие2 17:40-02.09.2018 01:00", tasks, new PrintStream(outContent));
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
        assertEquals(1, tasks.size());

    }

    private boolean wrongFormat(String command, ByteArrayOutputStream outContent) {
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand(command, tasks, new PrintStream(outContent));
        return tasks.size() == 0;
    }

    @Test
    public final void testWrongFormats() {  //Неверные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertTrue(wrongFormat("событие 13:40-02.09.2018", outContent)); // На входе не указывается продолжительность
        assertEquals("Неверный формат ввода: событие 13:40-02.09.2018\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("событие", outContent)); // На входе не указывается начало и продолжительность
        assertEquals("Неверный формат ввода: событие\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("", outContent)); // На входе пустая строка
        assertEquals("Неверный формат ввода: \n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("событие 11340-02.09.2018 01:30", outContent)); // На входе начало в неправильном формате
        assertEquals("Неверный формат даты: 11340-02.09.2018\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("событие 13:40-02.09.2018 11330", outContent)); // На входе продолжительность в неправильном формате
        assertEquals("Неверный формат продолжительности: 11330\n", outContent.toString());
    }

    @Test
    public final void testBadFormat() { // На входе часы больше 24 (лишние часы должны перейти в дни)
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        String task = "событие 25:40-02.09.2018 01:10";
        AddTask.doCommand(task, tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("01:40-03.09.2018"));
        Log log = tasks.get("01:40-03.09.2018");
        assertEquals("событие", log.task);
        assertEquals(DateWorker.getCorrectDate("01:40-03.09.2018", "HH:mm-dd.MM.yyyy"), log.startDate);
        assertEquals(DateWorker.getCorrectDate("02:50-03.09.2018", "HH:mm-dd.MM.yyyy"), log.endDate);
        assertEquals(false, log.check);
        assertEquals("Событие добавлено\n", outContent.toString());
    }


}
