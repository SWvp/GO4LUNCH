package com.kardabel.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.model.ChatMessageModel;
import com.kardabel.go4lunch.repository.ChatMessageRepository;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    @NonNull
    private final ChatMessageRepository chatMessageRepository;

    private final MediatorLiveData<List<ChatViewState>> chatMessagesMediatorLiveData = new MediatorLiveData<>();

    public ChatViewModel(@NonNull ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;

    }

    // RETRIEVE THE WORKMATE ID FROM THE CHAT ACTIVITY
    public void init(String workmateId) {

        LiveData<List<ChatMessageModel>> chatMessageModelLiveData = chatMessageRepository.getChatMessages(workmateId);

        chatMessagesMediatorLiveData.addSource(chatMessageModelLiveData, new Observer<List<ChatMessageModel>>() {
            @Override
            public void onChanged(List<ChatMessageModel> chatMessageModels) {
                combineMessages(chatMessageModels);
            }
        });
    }

    private void combineMessages(List<ChatMessageModel> chatMessageModels) {
        if (chatMessageModels != null) {
            chatMessagesMediatorLiveData.setValue(mapMessages(chatMessageModels));
        }
    }

    private List<ChatViewState> mapMessages(List<ChatMessageModel> chatMessageModels) {
        List<ChatViewState> chatViewStates = new ArrayList<>();

        for (ChatMessageModel chatMessageModel : chatMessageModels) {
            String message = chatMessageModel.getMessage();
            String date = chatMessageModel.getDate();

            chatViewStates.add(new ChatViewState(
                    message,
                    isSender(chatMessageModel.getSender()),
                    date));
        }

        return chatViewStates;
    }

    private int isSender(String sender) {
        int isSender = 1;
        String userId = GetCurrentUserIdUseCase.getCurrentUserUID();
        assert userId != null;
        if (userId.equals(sender)) {
            isSender = 2;
        }
        return isSender;
    }

    public LiveData<List<ChatViewState>> getChatMessages() {
        return chatMessagesMediatorLiveData;

    }
}
