package Data;
import com.google.cloud.Timestamp;

import java.util.*;

public class Log {
    public String task;
    public Date startDate;
    public Date endDate;
    public boolean check;

    public Log(String task, Date startDate, Date endDate, boolean check) {
        this.task = task;
        this.startDate = startDate;
        this.endDate = endDate;
        this.check = check;
    }

    public Log(String task, Date startDate, Date endDate) {
        this.task = task;
        this.startDate = startDate;
        this.endDate = endDate;
        this.check = false;
    }

    public Log(Map<String, Object> map) {
        task = map.get("task").toString();
        startDate = ((Timestamp)map.get("startDate")).toDate();
        endDate = ((Timestamp)map.get("endDate")).toDate();
        check = (Boolean)map.get("check");
    }
}
