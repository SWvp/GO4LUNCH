package com.kardabel.go4lunch.ui.chat;

import java.time.LocalDateTime;

public class ChatViewState {

    private final String chatMessageViewState;
    private final int chatMessageTypeViewState;
    private final LocalDateTime chatMessageTimeViewState;

    public ChatViewState(String chatMessageViewState, int chatMessageTypeViewState, LocalDateTime chatMessageTimeViewState) {
        this.chatMessageViewState = chatMessageViewState;
        this.chatMessageTypeViewState = chatMessageTypeViewState;
        this.chatMessageTimeViewState = chatMessageTimeViewState;
    }

    public String getChatMessageViewState() { return chatMessageViewState; }

    public int getChatMessageTypeViewState() { return chatMessageTypeViewState; }

    public LocalDateTime getChatMessageTimeViewState() { return chatMessageTimeViewState; }
}
