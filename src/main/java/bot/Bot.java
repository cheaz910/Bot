package bot;

import java.util.*;
import Data.Log;
import com.google.cloud.firestore.Firestore;

class Bot {
    private Firestore db = Firebase.Firebase.getDB();
    Map<String, Map<String, Log>> logAllUsers = Firebase.Firebase.gg(db);

    Map<String, Log> getLogForUser(String nameOfUser, Map<String, Map<String, Log>> log) {
        if (!log.containsKey(nameOfUser))
            log.put(nameOfUser, new HashMap());
        return log.get(nameOfUser);
    }

    String getHelp() {
        return  "Чтобы добавить событие, введите: +. \n" +
                "Чтобы добавить событие группе, введите: + группе. \n" +
                "Чтобы удалить событие, введите: -. \n" +
                "Чтобы удалить все события за день, введите: -день. \n" +
                "Чтобы удалить все события за месяц, введите: -месяц. \n" +
                "Чтобы удалить все события за год, введите: -год. \n" +
                "Чтобы перенести событие, введите: перенести. \n" +
                "Чтобы отметить выполненное событие, введите: выполнено. \n" +
                "Чтобы посмотреть все события за день, введите: день. \n" +
                "Чтобы посмотреть все события за месяц, введите: месяц. \n" +
                "Чтобы посмотреть праздники за день, введите: праздники. \n" +
                "Чтобы завершить работу, введите: спасибо. \n" +
                "Чтобы сохранить календарь, введите: сохранить. ";
    }

    void saveInfo() {
        Firebase.Firebase.uploadDB(logAllUsers, db);
    }
}
