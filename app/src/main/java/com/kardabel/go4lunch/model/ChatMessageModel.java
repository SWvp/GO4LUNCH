package com.kardabel.go4lunch.model;

public class ChatMessageModel {

    private String message;
    private String sender;
    private String date;
    private String timestamp;

    public ChatMessageModel() {}

    public ChatMessageModel(String message, String sender, String date, String timestamp) {
        this.message = message;
        this.sender = sender;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }

    public String getSender() { return sender; }

    public String getDate() { return date; }

}
