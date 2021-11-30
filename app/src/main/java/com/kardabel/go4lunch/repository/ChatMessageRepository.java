package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kardabel.go4lunch.model.ChatMessageModel;
import com.kardabel.go4lunch.model.UserModel;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatMessageRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<ChatMessageModel>> getChatMessages(String mateId) {

        String userId = GetCurrentUserIdUseCase.getCurrentUserUID();

        MutableLiveData<List<ChatMessageModel>> chatMessagesMutableLiveData = new MutableLiveData<>();

        Set<ChatMessageModel> chatMessages = new HashSet<>();


        // CREATE A LIST OF USER TO SORT THEM
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        ids.add(mateId);
        Collections.sort(ids);

        db.collection("chat")
                .document(ids.get(0) + "_" + ids.get(1))
                .collection(ids.get(0) + "_" + ids.get(1))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot value,
                            @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("messages error", error.getMessage());
                            return;
                        }

                        for (DocumentChange document : value.getDocumentChanges()) {
                                if (document.getType() == DocumentChange.Type.ADDED) {

                                    chatMessages.add(document.getDocument().toObject(ChatMessageModel.class));

                                }
                        }

                        List<ChatMessageModel> chatMessageModels = new ArrayList<>(chatMessages);
                        chatMessagesMutableLiveData.setValue(chatMessageModels);
                    }
                });


        return chatMessagesMutableLiveData;
    }
}