package bot;

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
        assertEquals(log.task, "событие");
        String pattern = "HH:mm-dd.MM.yyyy";
        assertEquals(log.startDate, DateWorker.getCorrectDate("13:40-02.09.2018", pattern));
        assertEquals(log.endDate, DateWorker.getCorrectDate("01:50-03.09.2018", pattern));
        assertEquals(log.check, false);
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
        assertEquals(log.task, "событие");
        assertEquals(log.startDate, DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy"));
        Date startDate = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        Date duration = DateWorker.getCorrectDate(strDate, "HH:mm");
        assertEquals(log.endDate, DateWorker.getEndDate(startDate, duration));
        assertEquals(log.check, false);
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
        assertEquals(log.task, "событие");
        assertEquals(log.startDate, DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy"));
        Date startDate = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        Date duration = DateWorker.getCorrectDate(strDate, "HH:mm");
        assertEquals(log.endDate, DateWorker.getEndDate(startDate, duration));
        assertEquals(log.check, false);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testInputWithoutDuration() {   // Входные данные без продолжительности
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие 13:40-02.09.2018 -", tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("13:40-02.09.2018"));
        Log log = tasks.get("13:40-02.09.2018");
        assertEquals(log.task, "событие");
        assertEquals(log.startDate, DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.endDate, DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.check, false);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    @Test
    public final void testInputWithFewWords() {   // Входные данные с задачей, состоящей из более одного слова
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        AddTask.doCommand("событие1 событие2 13:40-02.09.2018 12:10", tasks, new PrintStream(outContent));
        assertTrue(tasks.containsKey("13:40-02.09.2018"));
        Log log = tasks.get("13:40-02.09.2018");
        assertEquals(log.task, "событие1 событие2");
        assertEquals(log.startDate, DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.endDate, DateWorker.getCorrectDate("01:50-03.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.check, false);
        assertEquals("Событие добавлено\n", outContent.toString());
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
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("событие", outContent)); // На входе не указывается начало и продолжительность
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("", outContent)); // На входе пустая строка
        assertEquals("Неверный формат ввода\n", outContent.toString());
        outContent.reset();
        assertTrue(wrongFormat("событие 11340-02.09.2018 01:30", outContent)); // На входе начало в неправильном формате
        assertEquals("Неверный формат даты: 11340-02.09.2018 01:30\n", outContent.toString());
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
        assertEquals(log.task, "событие");
        assertEquals(log.startDate, DateWorker.getCorrectDate("01:40-03.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.endDate, DateWorker.getCorrectDate("02:50-03.09.2018", "HH:mm-dd.MM.yyyy"));
        assertEquals(log.check, false);
        assertEquals("Событие добавлено\n", outContent.toString());
    }

    private Map<String, Log> getTasksAfterAdding(String secondCommand, ByteArrayOutputStream outContent) {
        PrintStream outputStream = new PrintStream(outContent);
        Map<String, Log> tasks = new HashMap<>();
        String command = "событие 13:40-02.09.2018 01:10";
        AddTask.doCommand(command, tasks, outputStream);
        AddTask.doCommand(secondCommand, tasks, outputStream);
        return tasks;
    }

    @Test
    public final void testIsConflict_NoIntersection() { // Два события не пересекаются
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(2, getTasksAfterAdding("событие2 12:10-02.09.2018 1:10", outContent).size());
        assertEquals("Событие добавлено\nСобытие добавлено\n", outContent.toString());
    }


    @Test
    public final void testIsConflict_OneIntersection() { // События пересекаются в одной точке
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, getTasksAfterAdding("событие2 12:10-02.09.2018 1:30", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }


    @Test
    public final void testIsConflict_Intersection1() { // Конец одного события находится в промежутке другого события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, getTasksAfterAdding("событие2 12:10-02.09.2018 01:35", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }

    @Test
    public final void testIsConflict_Nesting() { // Одно событие находится в промежутке другого события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        assertEquals(1, getTasksAfterAdding("событие2 13:45-02.09.2018 00:30", outContent).size());
        assertEquals("Событие добавлено\nНа это время уже запланировано событие\n", outContent.toString());
    }
}
