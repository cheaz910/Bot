package Commands;

import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;

public class GetHolidaysTest {
    @Test
    public final void testDefault() { // Стандартные входные данные
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(outContent);
        GetHolidays.doCommand("01.11", outputStream);
        assertEquals("Международный день энергосбережения\n" +
                "День офтальмолога\n" +
                "День окончания Первой мировой войны\n" +
                "Аврамий Овчар и Анастасия Овечница\n" +
                "День написания бумажных писем\n" +
                "Всемирный день мини-лыж\n", outContent.toString());
    }

    @Test
    public final void testWrongFormat() { // Неверный формат даты
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(outContent);
        GetHolidays.doCommand("01.16", outputStream);
        assertEquals("Неверный формат ввода: 01.16\n", outContent.toString());
    }
}