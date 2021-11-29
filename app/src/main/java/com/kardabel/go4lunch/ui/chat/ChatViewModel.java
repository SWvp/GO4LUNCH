package com.kardabel.go4lunch.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final MediatorLiveData<List<ChatViewState>> chatMessagesMediatorLiveData = new MediatorLiveData<>();




    // RETRIEVE THE WORKMATE ID FROM THE CHAT ACTIVITY
    public void init(String workmateId) {

    }









    public LiveData<List<ChatViewState>> getChatMessages() {
        return chatMessagesMediatorLiveData;

    }
}
