package com.gluonhq.chat.service;

import com.gluonhq.chat.model.ChatImage;
import com.gluonhq.chat.model.ChatMessage;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class DummyService implements Service {

    @Override
    public ObservableList<ChatMessage> getMessages() {
        return FXCollections.observableArrayList(
                new ChatMessage("Message 1", "Author 1"),
                new ChatMessage("Message 2", "Author 2"),
                new ChatMessage("Message 3", "Author 3")
        );
    }

    @Override
    public GluonObservableList<ChatMessage> getMessages(Consumer<GluonObservableList<ChatMessage>> consumer) {
        return null;
    }

    @Override
    public ObservableList<ChatImage> getImages() {
        return FXCollections.observableArrayList(
                ImageUtils.encodeImage("1", new Image("/icon.png")),
                ImageUtils.encodeImage("2", new Image("/icon.png")),
                ImageUtils.encodeImage("3", new Image("/icon.png"))
        );
    }

    @Override
    public String addImage(String id, Image image) {
        // no-op
        return id;
    }

    @Override
    public boolean saveUser(String userName) {
        return false;
    }

    @Override
    public String getName() {
        return "Dummy Name";
    }

    @Override
    public String getName(Consumer<GluonObservableObject<String>> consumer) {
        return getName();
    }
}
