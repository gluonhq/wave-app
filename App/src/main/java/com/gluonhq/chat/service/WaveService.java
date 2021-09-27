package com.gluonhq.chat.service;

import com.gluonhq.chat.model.Channel;
import com.gluonhq.chat.model.ChatImage;
import com.gluonhq.chat.model.ChatMessage;
import com.gluonhq.chat.model.User;
import com.gluonhq.wave.Contact;
import com.gluonhq.wave.WaveManager;
import com.gluonhq.wave.message.MessagingClient;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javafx.collections.ListChangeListener;

public class WaveService implements Service, MessagingClient {

    private User loggedUser;
    private final WaveManager wave;
    ObservableList<Channel> channels =  FXCollections.observableArrayList();
    boolean channelsClean = false;

    public WaveService () {
        wave = new WaveManager();
        if (wave.isInitialized()) {
            login("You");
            registerListeners();
        }
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
        ObservableList<ChatMessage> answer = channel.getMessages();
        if (answer.size() == 0) {
            List<ChatMessage> intro = channel.getMembers().stream()
                    .map(user -> new ChatMessage("Message from " + user.displayName(), user))
                    .collect(Collectors.toList());
            answer.addAll(intro);
        }
//        ObservableList<ChatMessage>  answer = channel.getMembers().stream()
//                .map(user -> new ChatMessage("Message from " + user.displayName(), user))
//                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        answer.addListener(new ListChangeListener<ChatMessage>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ChatMessage> change) {
                while (change.next()) {
                    List<? extends ChatMessage> subList = change.getAddedSubList();
                    System.err.println("ADDED message for channel "+ channel+": "+change.getAddedSubList());
                    System.err.println("Members in this channel: " + channel.getMembers());
                    for (ChatMessage message : subList) {
                        if (message.isLocalOriginated()) {
                            
                            String uuid = channel.getMembers().get(0).getId();
                            System.err.println("UUID = "+uuid);
                            wave.sendMessage(uuid, message.getMessage());
                        }
                    }
                }
            }
        });
        return answer;
    }

    @Override
    public boolean login(String username) {
        this.loggedUser = new User(username, "First Name", "Last Name");
        return true;
    }

    @Override
    public ObservableList<User> getUsers() {
        System.err.println("GET USERS!");
        ObservableList<User> answer = FXCollections.observableArrayList();
//        for (Contact a: wave.getContacts()){
//            User user = new User(a.getUuid(), a.getName(), a.getName(), a.getName());
//        }
System.err.println("CONTACTS = " + wave.getContacts());   
if (wave.getContacts() != null) {
    Contact c0 = wave.getContacts().get(0);
    System.err.println("C0 = "+c0.getUuid()+" with name =  "+c0.getName()+" and nr = "+c0.getNr());
}
answer.addAll(wave.getContacts().stream()
                .map(a -> new User(a.getUuid(), a.getName(), a.getName(), a.getName()))
                .collect(toList()));
        System.err.println("user0uuid = "+answer.get(0).getId());
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
        if (!channelsClean) {
            channels =  FXCollections.observableArrayList();
        channels.addAll(
                new Channel("general", getUsers()),
                new Channel("notification", getUsers().filtered(u -> u.getUsername().startsWith("j"))),
                new Channel("track", getUsers().filtered(u -> !u.getUsername().startsWith("j"))));
        
        channels.addAll(getUsers().stream().map(user -> new Channel(user) ).collect(toList()));
        channelsClean = true;
        }
        return channels;
    }

    @Override
    public User loggedUser() {
        return loggedUser;
    }

    void registerListeners() {
        this.wave.setMessageListener(this);
    }
    /*@Override
    public String getName(Consumer<ObjectProperty<String>> consumer) {
        consumer.accept(new SimpleObjectProperty<>("name"));
        return getName();
    }*/

    @Override
    public void gotMessage(String senderUuid, String content) {
        System.err.println("GOT MESSAGE from "+senderUuid+" with content "+content);
        System.err.println("Channels = "+this.channels);
        Channel dest = this.channels.stream()
                .filter(c -> c.getMembers().size() > 0)
                .filter(c -> c.getMembers().get(0).getId().equals(senderUuid))
                .findFirst().get();
        ChatMessage chatMessage = new ChatMessage(content, dest.getMembers().get(0));
        dest.getMessages().add(chatMessage);
        System.err.println("Message is for "+dest+" and its messages are now "+dest.getMessages());
    }
}
