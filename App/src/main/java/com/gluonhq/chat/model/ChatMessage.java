package com.gluonhq.chat.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChatMessage extends Searchable {

    String id;
    String message;
    LocalDateTime time;
    User user;

    public ChatMessage(String message, User user) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.time = randomDateTime();
        this.user = user;
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
    
    // TODO: Remove
    LocalDateTime randomDateTime() {
        long minDay = LocalDateTime.of(1970, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long maxDay = LocalDateTime.of(2021, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long randomTime = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDateTime.ofEpochSecond(randomTime, 0, ZoneOffset.UTC);
    }
}
