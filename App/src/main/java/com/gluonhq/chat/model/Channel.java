package com.gluonhq.chat.model;

import javafx.collections.ObservableList;

public class Channel {

    private ObservableList<User> members;
    private ObservableList<ChatMessage> messages;

    public ObservableList<User> getMembers() {
        return members;
    }

    public void setMembers(ObservableList<User> members) {
        this.members = members;
    }

    public ObservableList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ObservableList<ChatMessage> messages) {
        this.messages = messages;
    }
}
