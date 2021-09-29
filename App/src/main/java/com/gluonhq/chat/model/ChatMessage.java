package com.gluonhq.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ChatMessage extends Searchable {

    boolean localOriginated = false;
    String id;
    String message;
    LocalDateTime time;
    User user;

    public ChatMessage() {
    }

    public ChatMessage(String message, User user) {
        this (message, user, false);
    }
    
    public ChatMessage(String message, User user, boolean local) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.time = LocalDateTime.now();
        this.user = user;
        this.localOriginated = local;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;
        ChatMessage that = (ChatMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", user=" + user.toString() +
                '}';
    }

    @Override
    public boolean contains(String keyword) {
        return containsKeyword(getMessage(), keyword);
    }

    public String getFormattedTime() {
        return getTime().toLocalDate().toString() + " " +
                String.format("%02d:%02d", getTime().getHour(),getTime().getMinute());
    }
    
    public boolean isLocalOriginated() {
        return localOriginated;
    }
}
