import java.text.DateFormat;

public class Log {
    public DateFormat datetime;
    public String record;

    public Log(DateFormat datetime, String record) {
        this.datetime = datetime;
        this.record = record;
    }
}
