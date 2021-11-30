package com.kardabel.go4lunch.usecase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddChatMessageToFirestoreUseCase {

    @NonNull
    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection("chat");
    }

    public static void createChatMessage(String message, String mateId) {

        String userId = GetCurrentUserIdUseCase.getCurrentUserUID();

        // CREATE A LIST OF USER TO SORT THEM
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        ids.add(mateId);
        Collections.sort(ids);

        // RETRIEVE THE CURRENT DATE AND TIME AND FORMAT TO HUMAN READABLE
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm:ss");
        String formatDateTime = currentDateTime.format(formatter);

        // CREATE MAP TO SEND TO FIRESTORE
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("message", message);
        chatMessage.put("sender", userId);
        chatMessage.put("date", formatDateTime);
        chatMessage.put("timestamp" , FieldValue.serverTimestamp());

        // CREATE MESSAGE IN DATA BASE
        getChatCollection()
                .document(ids.get(0) + "_" + ids.get(1))
                .collection(ids.get(0) + "_" + ids.get(1))
                .add(chatMessage)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with message: " + message))
                .addOnFailureListener(e ->
                        Log.d(TAG, "Error adding document", e));
    }
}
