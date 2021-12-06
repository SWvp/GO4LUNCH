package com.kardabel.go4lunch.ui.chat;

public class ChatViewState {

    private final String chatMessageViewState;
    private final int isSender;
    private final String chatMessageTimeViewState;
    private final Long timestamp;

    public ChatViewState(String chatMessageViewState, int isSender, String chatMessageTimeViewState, Long timestamp) {
        this.chatMessageViewState = chatMessageViewState;
        this.isSender = isSender;
        this.chatMessageTimeViewState = chatMessageTimeViewState;
        this.timestamp = timestamp;
    }

    public String getChatMessageViewState() { return chatMessageViewState; }

    public int getChatMessageTypeViewState() { return isSender; }

    public String getChatMessageTimeViewState() { return chatMessageTimeViewState; }

    public Long getTimestamp() {
        return timestamp;
    }
}
