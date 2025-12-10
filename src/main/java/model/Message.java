package model;

public class Message {
    private int id;
    private String sessionId;
    private String sender;
    private String message;
    private String timestamp;

    public Message(String sessionId, String sender, String message) {
        this.sessionId = sessionId;
        this.sender = sender;
        this.message = message;
    }

    public Message(int id, String sessionId, String sender, String message, String timestamp) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
