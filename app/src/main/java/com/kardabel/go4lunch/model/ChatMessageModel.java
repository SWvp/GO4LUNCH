package com.kardabel.go4lunch.model;


import java.time.LocalDateTime;

public class ChatMessageModel {

    private String message;
    private int messageType;
    private LocalDateTime messageTime;

    public ChatMessageModel() {}

    public ChatMessageModel(String message, int messageType, LocalDateTime messageTime) {
        this.message = message;
        this.messageType = messageType;
        this.messageTime = messageTime;
    }

    public String getMessage() { return message; }

    public int getMessageType() { return messageType; }

    public LocalDateTime getMessageTime() { return messageTime; }

}
