package Firebase;

import Data.Log;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class Firebase {
    public static String documentName = "users";

    public static Firestore getDB() {
        try {
            FileInputStream serviceAccount = new FileInputStream("servicekey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://telegrambot-46b3f.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
            Firestore db = FirestoreClient.getFirestore();
            return db;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> downloadDB(Firestore db) {
        DocumentSnapshot document = null;
        try {
            document = db.collection(documentName).document(documentName).get().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> map = (HashMap<String, Object>)document.getData();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> result = new ConcurrentHashMap<>();
        if (map != null) {
            for (String key : map.keySet()) {
                result.put(key, convertFirebaseMap((HashMap<String, Object>)map.get(key)));
            }
        }
        return result;
    }

    private static ConcurrentHashMap<String, Log> convertFirebaseMap(HashMap<String, Object> map) {
        ConcurrentHashMap<String, Log> result = new ConcurrentHashMap<>();
        for (String key : map.keySet()) {
            result.put(key, new Log((HashMap<String, Object>)map.get(key)));
        }
        return result;
    }

    public static void uploadDB(ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> map, Firestore db) {
        synchronized (map) {
            ConcurrentHashMap<String, ConcurrentHashMap<String, Log>> otherMap = downloadDB(db);
            for (String key : otherMap.keySet()) {
                if (!map.containsKey(key)) {
                    map.put(key, otherMap.get(key));
                } else {
                    ConcurrentHashMap<String, Log> internalOtherMap = otherMap.get(key);
                    ConcurrentHashMap<String, Log> internalMap = map.get(key);
                    for (String internalKey : internalOtherMap.keySet()) {
                        if (!internalMap.containsKey(internalKey)) {
                            internalMap.put(internalKey, internalOtherMap.get(internalKey));
                        }
                    }
                }
            }
        }
        ApiFuture<WriteResult> future = db.collection(documentName).document(documentName).set(map);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
