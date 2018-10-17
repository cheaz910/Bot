package bot;
import java.util.*;

class Log {
    public String note;
    public Date startDate;
    public Date endDate;
    public boolean check;

    Log(String note, Date startDate, Date endDate, boolean check) {
        this.note = note;
        this.startDate = startDate;
        this.endDate = endDate;
        this.check = check;
    }

    Log(String note, Date startDate, Date endDate) {
        this.note = note;
        this.startDate = startDate;
        this.endDate = endDate;
        this.check = false;
    }
}
