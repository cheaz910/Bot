package Firebase;

import Data.Log;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import javax.swing.text.Document;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Firebase {
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

    public static Map<String, Map<String, Log>> gg(Firestore db) {
        DocumentSnapshot document = null;
        try {
            document = db.collection("users").document("users").get().get();
            System.out.println("1");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = document.getData();
        Map<String, Map<String, Log>> result = new HashMap<>();
        for (String key : map.keySet()) {
            result.put(key, convertFirebaseMap((Map<String, Object>)map.get(key)));
        }
        return result;
    }

    private static Map<String, Log> convertFirebaseMap(Map<String, Object> map) {
        Map<String, Log> result = new HashMap<>();
        for (String key : map.keySet()) {
            result.put(key, new Log((Map<String, Object>)map.get(key)));
        }
        return result;
    }

    public static void uploadDB(Map<String, Map<String, Log>> map, Firestore db) {
        ApiFuture<WriteResult> future = db.collection("users").document("users").set(map);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
