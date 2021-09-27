package com.gluonhq.chat.service;

import com.gluonhq.chat.model.Channel;
import com.gluonhq.chat.model.ChatImage;
import com.gluonhq.chat.model.ChatMessage;
import com.gluonhq.chat.model.User;
import com.gluonhq.wave.Contact;
import com.gluonhq.wave.WaveManager;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javafx.collections.ListChangeListener;

public class WaveService implements Service {

    private User loggedUser;
    private WaveManager wave;

    public WaveService () {
        wave = new WaveManager();
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
    public ObservableList<ChatMessage> getMessages(Channel channel) {
        return channel.getMembers().stream()
                .map(user -> new ChatMessage("Message from " + user.displayName(), user))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @Override
    public boolean login(String username) {
        this.loggedUser = new User(username, "First Name", "Last Name");
        return true;
    }

    @Override
    public ObservableList<User> getUsers() {
        ObservableList<User> answer = FXCollections.observableArrayList();
        answer.addAll(wave.getContacts().stream().map((Contact a) -> new User(a.getUuid(), a.getName(), a.getName(), a.getName())).collect(toList()));
        wave.getContacts().addListener(new ListChangeListener<Contact>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Contact> change) {
                while (change.next()) {
                    change.getAddedSubList().forEach(c -> answer.add(0, new User("d", "e", "f")));
                }
            }
        });
        
        return answer;
        
    }

    @Override
    public ObservableList<Channel> getChannels() {
        ObservableList<Channel> answer =  FXCollections.observableArrayList();
        answer.addAll(
                new Channel("general", getUsers()),
                new Channel("notification", getUsers().filtered(u -> u.getUsername().startsWith("j"))),
                new Channel("track", getUsers().filtered(u -> !u.getUsername().startsWith("j"))));
        
        answer.addAll(getUsers().stream().map(user -> new Channel(user) ).collect(toList()));
        return answer;
    }

    @Override
    public User loggedUser() {
        return loggedUser;
    }

    /*@Override
    public String getName(Consumer<ObjectProperty<String>> consumer) {
        consumer.accept(new SimpleObjectProperty<>("name"));
        return getName();
    }*/
}
