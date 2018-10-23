package bot;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RemoveTasksTest {

    @Test
    public final void testRemoveOneTask_Default() { // Удаление существующего события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        RemoveTasks.removeOneTask("13:40-02.09.2018", tasks, new PrintStream(outContent));
        assertEquals("Событие удалено\n", outContent.toString());
        assertEquals(0, tasks.size());
    }

    @Test
    public final void testRemoveOneTask_InputInAbbreviatedForm() { // Удаление существующего события в сокращённом формате даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String strDate1 = "13:40-02." + month + "." + year;
        String strDate2 = "14:40-02.07." + year;
        Date date1 = DateWorker.getCorrectDate(strDate1, "HH:mm-dd.MM.yyyy");
        Date date2 = DateWorker.getCorrectDate(strDate2, "HH:mm-dd.MM.yyyy");
        tasks.put(strDate1, new Log("событие1", date1, new Date(0)));
        tasks.put(strDate2, new Log("событие2", date2, new Date(0)));
        RemoveTasks.removeOneTask(strDate1, tasks, new PrintStream(outContent));
        RemoveTasks.removeOneTask(strDate2, tasks, new PrintStream(outContent));
        assertEquals("Событие удалено\nСобытие удалено\n", outContent.toString());
        assertEquals(0, tasks.size());
    }

    @Test
    public final void testRemoveOneTask_NotExist() { // Удаление не существующего события
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        RemoveTasks.removeOneTask("13:45-02.09.2018", tasks, new PrintStream(outContent));
        assertEquals("Такого события нет\n", outContent.toString());
        assertEquals(1, tasks.size());
    }

    @Test
    public final void testRemoveOneTask_WrongFormat() { // Неправильный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        RemoveTasks.removeOneTask("13:41231202.09.2018", tasks, new PrintStream(outContent));
        assertEquals("Неверный формат даты: 13:41231202.09.2018", outContent.toString());
        assertEquals(1, tasks.size());
    }

    private Map<String, Log> getTasks() {
        Map<String, Log> tasks = new HashMap<>();
        tasks.put("13:40-02.09.2018", new Log("событие1", new Date(0), new Date(0)));
        tasks.put("20:40-03.10.2018", new Log("событие2", new Date(0), new Date(0)));
        tasks.put("20:00-04.10.2019", new Log("событие3", new Date(0), new Date(0)));
        return tasks;
    }

    @Test
    public final void testRemoveTasksOfDayMonthYear_Day() { // Удаление событий за день
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = getTasks();
        RemoveTasks.removeTasksOfDayMonthYear("03.10.2018", tasks, "dd.MM.yyyy", new PrintStream(outContent));
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(2, tasks.size());
    }

    @Test
    public final void testRemoveTasksOfDayMonthYear_Month() { // Удаление событий за месяц
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = getTasks();
        RemoveTasks.removeTasksOfDayMonthYear("10.2018", tasks, "MM.yyyy", new PrintStream(outContent));
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(2, tasks.size());
    }

    @Test
    public final void testRemoveTasksOfDayMonthYear_Year() { // Удаление событий за год
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = getTasks();
        RemoveTasks.removeTasksOfDayMonthYear("2018", tasks, "yyyy", new PrintStream(outContent));
        assertEquals("События удалены\n", outContent.toString());
        assertEquals(1, tasks.size());
    }

    @Test
    public final void testRemoveTasksOfDayMonthYear_WrongFormat() { // Неправильный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        Map<String, Log> tasks = new HashMap<>();
        tasks.put("13:40-02.09.2018", new Log("событие", new Date(0), new Date(0)));
        RemoveTasks.removeTasksOfDayMonthYear("13:41231202.09.2018", tasks, "yyyy", new PrintStream(outContent));
        assertEquals("Неверный формат даты: 13:41231202.09.2018", outContent.toString());
        assertEquals(1, tasks.size());
    }

}
