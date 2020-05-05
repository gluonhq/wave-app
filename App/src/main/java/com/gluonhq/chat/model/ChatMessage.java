package com.gluonhq.chat.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class ChatMessage {

    String id;
    String message;
    LocalDateTime time;
    String author;
    String timeZone;

    public ChatMessage() {
    }

    public ChatMessage(String message, String author) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.time = LocalDateTime.now();
        this.author = author;
        this.timeZone = TimeZone.getDefault().getID();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public long getEpochMillis() {
        return ZonedDateTime.of(time, ZoneId.of(timeZone)).toEpochSecond() * 1000;
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
                ", timeZone=" + timeZone +
                ", author=" + author +
                '}';
    }
}
