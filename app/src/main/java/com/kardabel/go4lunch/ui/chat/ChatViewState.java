package com.kardabel.go4lunch.ui.chat;

import java.time.LocalDateTime;

public class ChatViewState {

    private final String chatMessageViewState;
    private final int isSender;
    private final String chatMessageTimeViewState;

    public ChatViewState(String chatMessageViewState, int isSender, String chatMessageTimeViewState) {
        this.chatMessageViewState = chatMessageViewState;
        this.isSender = isSender;
        this.chatMessageTimeViewState = chatMessageTimeViewState;
    }

    public String getChatMessageViewState() { return chatMessageViewState; }

    public int getChatMessageTypeViewState() { return isSender; }

    public String getChatMessageTimeViewState() { return chatMessageTimeViewState; }
}
