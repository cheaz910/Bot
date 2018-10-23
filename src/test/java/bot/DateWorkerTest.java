package bot;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DateWorkerTest {
    @Test
    public final void testRecalculateEndDate_Default() { // Стандартные входные данные
        Date startOldDate = new Date(2018, 9,2,13,40);
        Date endOldDate = new Date(2018, 9, 2, 15,45);
        Date startDate = new Date(2019,10,3,15,45);
        Date endDate = DateWorker.recalculateEndDate(startOldDate, endOldDate, startDate);
        assertEquals(2019, endDate.getYear());
        assertEquals(10, endDate.getMonth());
        assertEquals(3, endDate.getDate());
        assertEquals(17, endDate.getHours());
        assertEquals(50, endDate.getMinutes());
    }

    @Test
    public final void testGetCorrectDate_Default() { // Стандартные входные данные
        String strDate = "15:30-02.09.2018";
        Date date = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        assertEquals(new Date(118, 8, 2, 15, 30), date);
    }

    @Test
    public final void testGetCorrectDate_WrongFormat() { // Неверный формат даты
        String strDate = "15:30----юю02.09.2018";
        Date date = DateWorker.getCorrectDate(strDate, "HH:mm-dd.MM.yyyy");
        assertNull(date);
    }


    @Test
    public final void testGetEndDate_LowDuration() { // Продолжительность до 24 часов
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,2,30);
        Date endDate = DateWorker.getEndDate(startDate, duration);
        assertEquals(16, endDate.getHours());
        assertEquals(10, endDate.getMinutes());
    }

    @Test
    public final void testGetEndDate_HighDuration() { // Продолжительность больше 24 часоd
        Date startDate = new Date(2018, 9,2,13,40);
        Date duration = new Date(0,0,0,25,30);
        Date endDate = DateWorker.getEndDate(startDate, duration);
        assertEquals(15, endDate.getHours());
        assertEquals(10, endDate.getMinutes());
    }

    private Map<String, Log> getTasks() {
        Map<String, Log> tasks = new HashMap<>();
        Date strStartDate = DateWorker.getCorrectDate("13:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        Date strEndDate = DateWorker.getCorrectDate("14:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        tasks.put("13:40-02.09.2018", new Log("событие", strStartDate, strEndDate));
        return tasks;
    }

    @Test
    public final void testIsConflict_NoIntersection() { // Два события не пересекаются
        Map<String, Log> tasks = getTasks();
        Date strStartDate = DateWorker.getCorrectDate("15:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        Date strEndDate = DateWorker.getCorrectDate("16:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        assertTrue(!DateWorker.isConflict(tasks, new Log("событие", strStartDate, strEndDate)));
    }


    @Test
    public final void testIsConflict_OneIntersection() { // События пересекаются в одной точке
        Map<String, Log> tasks = getTasks();
        Date strStartDate = DateWorker.getCorrectDate("14:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        Date strEndDate = DateWorker.getCorrectDate("16:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        assertTrue(DateWorker.isConflict(tasks, new Log("событие", strStartDate, strEndDate)));
    }


    @Test
    public final void testIsConflict_Intersection1() { // Конец одного события находится в промежутке другого события
        Map<String, Log> tasks = getTasks();
        Date strStartDate = DateWorker.getCorrectDate("14:00-02.09.2018", "HH:mm-dd.MM.yyyy");
        Date strEndDate = DateWorker.getCorrectDate("16:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        assertTrue(DateWorker.isConflict(tasks, new Log("событие", strStartDate, strEndDate)));
    }

    @Test
    public final void testIsConflict_Nesting() { // Одно событие находится в промежутке другого события
        Map<String, Log> tasks = getTasks();
        Date strStartDate = DateWorker.getCorrectDate("13:00-02.09.2018", "HH:mm-dd.MM.yyyy");
        Date strEndDate = DateWorker.getCorrectDate("16:40-02.09.2018", "HH:mm-dd.MM.yyyy");
        assertTrue(DateWorker.isConflict(tasks, new Log("событие", strStartDate, strEndDate)));
    }


}
