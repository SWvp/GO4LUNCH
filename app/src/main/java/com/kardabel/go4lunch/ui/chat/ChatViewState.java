package com.kardabel.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ChatViewState {

    private final String chatMessageViewState;
    private final int isSender;
    private final String chatMessageTimeViewState;
    private final Long timestamp;

    public ChatViewState(
            String chatMessageViewState,
            int isSender,
            String chatMessageTimeViewState,
            Long timestamp) {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatViewState that = (ChatViewState) o;
        return isSender == that.isSender &&
                Objects.equals(chatMessageViewState, that.chatMessageViewState) &&
                Objects.equals(chatMessageTimeViewState, that.chatMessageTimeViewState) &&
                Objects.equals(timestamp, that.timestamp);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                chatMessageViewState,
                isSender,
                chatMessageTimeViewState,
                timestamp);

    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
                "chatMessageViewState='" + chatMessageViewState + '\'' +
                ", isSender=" + isSender +
                ", chatMessageTimeViewState='" + chatMessageTimeViewState + '\'' +
                ", timestamp=" + timestamp +
                '}';

    }
}
