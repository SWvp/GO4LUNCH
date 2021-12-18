package com.kardabel.go4lunch.usecase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddChatMessageToFirestoreUseCase {

    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;
    private final Clock clock;

    public static final String MESSAGE = "message";
    public static final String SENDER = "sender";
    public static final String DATE = "date";
    public static final String TIMESTAMP = "timestamp";
    public static final String COLLECTION_CHAT = "chat";

    public AddChatMessageToFirestoreUseCase(FirebaseFirestore firebaseFirestore,
                                            FirebaseAuth firebaseAuth,
                                            Clock clock) {

        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.clock = clock;

    }

    @NonNull
    public CollectionReference getChatCollection() {
        return firebaseFirestore.collection(COLLECTION_CHAT);
    }

    public void createChatMessage(String message, String workmateId) {

        String userId = firebaseAuth.getCurrentUser().getUid();

        // CREATE A LIST OF USER TO SORT THEM
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        ids.add(workmateId);
        Collections.sort(ids);

        // RETRIEVE THE CURRENT DATE AND TIME, AND FORMAT TO HUMAN READABLE
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm:ss");
        String formatDateTime = currentDateTime.format(formatter);

        // CREATE MAP TO SEND TO FIRESTORE
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put(MESSAGE, message);
        chatMessage.put(SENDER, userId);
        chatMessage.put(DATE, formatDateTime);
        chatMessage.put(TIMESTAMP, Instant.now(clock).toEpochMilli()); // HERE IS THE TIMESTAMP NEEDED TO SORT THE CHAT MESSAGE

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
