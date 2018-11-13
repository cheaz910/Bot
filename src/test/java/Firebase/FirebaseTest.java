package Firebase;

import Data.Log;
import com.google.cloud.firestore.Firestore;
import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FirebaseTest {
    @Test
    public final void testGetLogForUser_NewUser() { // Новый пользователь, которого еще нет в логе
        Firestore db = Firebase.getDB();
        Firebase.documentName = "FirebaseTest";
        ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> map = Firebase.downloadDB(db);
        ConcurrentHashMap<String, Log> logOneUser = new ConcurrentHashMap<>();
        logOneUser.put("событие123", new Log("событие123", new Date(0), new Date(0)));
        map.put("user1", logOneUser);
        Firebase.uploadDB(map, db);
        ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> map1 = Firebase.downloadDB(db);
        assertArrayEquals(map.get("user1").get("событие123").getClass().getFields(),
                map1.get("user1").get("событие123").getClass().getFields());
    }
}
