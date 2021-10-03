package com.gluonhq.chat.service;

import com.gluonhq.chat.model.Channel;
import com.gluonhq.chat.model.ChatImage;
import com.gluonhq.chat.model.ChatMessage;
import com.gluonhq.chat.model.User;
import com.gluonhq.chat.views.LoginPresenter;

import com.gluonhq.wave.Contact;
import com.gluonhq.wave.QRGenerator;
import com.gluonhq.wave.WaveManager;
import com.gluonhq.wave.message.MessagingClient;
import com.gluonhq.wave.provisioning.ProvisioningClient;
import java.time.LocalDateTime;
import java.security.Security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class WaveService implements Service, ProvisioningClient, MessagingClient {
    
    private User loggedUser;
    private final WaveManager wave;
    ObservableList<Channel> channels = FXCollections.observableArrayList();
    Map<Channel, ObservableList<ChatMessage>> channelMap = new HashMap<>();
    boolean channelsClean = false;
    private ObservableList<Contact> contacts;
    private LoginPresenter loginPresenter;
    
    static {
        Security.addProvider(new BouncyCastleProvider());
        Security.setProperty("crypto.policy", "unlimited");
    }

    public WaveService() {
        wave = WaveManager.getInstance();
        System.err.println("Creating waveService: " + System.identityHashCode(wave));
        if (wave.isInitialized()) {
            login("You");
            this.wave.setMessageListener(this);
        }
    }

    @Override
    public ObservableList<ChatImage> getImages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean login(String userName) {
        this.loggedUser = new User(userName, "First Name", "Last Name");
        return true;
    }
   
    @Override
    public ObservableList<User> getUsers() {
        ObservableList<User> answer = FXCollections.observableArrayList();
        contacts = wave.getContacts();

        answer.addAll(contacts.stream()
                .map(a -> createUserFromContact(a))
                .collect(Collectors.toList()));

        contacts.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                answer.clear();
                answer.addAll(contacts.stream()
                        .map(a -> createUserFromContact(a))
                        .collect(Collectors.toList()));
            }
        });
        return answer;
    }

    @Override
    public ObservableList<Channel> getChannels() {
        if (!channelsClean) {
            channels = FXCollections.observableArrayList();
            ObservableList<User> users = getUsers();
            channels.addAll(users.stream().map(user -> createChannelFromUser(user))
                    .collect(Collectors.toList()));

            users.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    Platform.runLater(() -> {
                        channels.clear();
                        channelMap.clear();
                        channels.addAll(users.stream().map(user -> createChannelFromUser(user))
                                .collect(Collectors.toList()));
                    });
                }
            });

            channelsClean = true;
        }
        return channels;
    }

    @Override
    public User loggedUser() {
        return loggedUser;
    }

    @Override
    public void gotMessage(String senderUuid, String content) {
                System.err.println("GOT MESSAGE from " + senderUuid + " with content " + content);
        Channel dest = this.channels.stream()
                .filter(c -> c.getMembers().size() > 0)
                .filter(c -> c.getMembers().get(0).getId().equals(senderUuid))
                .findFirst().get();
        ChatMessage chatMessage = new ChatMessage(content, dest.getMembers().get(0), LocalDateTime.now());
        Platform.runLater(() -> dest.getMessages().add(chatMessage));
        System.err.println("Message is for " + dest + " and its messages are now " + dest.getMessages());
    }

    @Override
    public void bootstrap(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
        Runnable r = () -> wave.startProvisioning(this);
        Thread t = new Thread(r);
        t.start();
    }
    
    private Channel createChannelFromUser(User u) {
        ObservableList<ChatMessage> messages = FXCollections.observableArrayList();
        Channel answer = new Channel(u, messages);
        channelMap.put(answer, messages);
        messages.addListener(new ListChangeListener<ChatMessage>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ChatMessage> change) {
                while (change.next()) {
                    System.err.println("Added list: " + change.getAddedSubList());
                    change.getAddedSubList().stream().filter(m -> m.getLocalOriginated())
                            .forEach(m -> {
                                String uuid = u.getId();
                                System.err.println("UUID = " + u);
                                wave.sendMessage(uuid, m.getMessage());
                            });
                }
            }

        });
        return answer;
    }
    
    private static User createUserFromContact(Contact c) {
        String firstName = c.getName();
        if ((c.getName() == null) || (c.getName().isEmpty())) {
            firstName = c.getNr();
            if ((firstName == null) || (firstName.isEmpty())) {
                firstName = c.getUuid();
            }
        }
        User answer = new User(firstName, firstName, "", c.getUuid());
        return answer;
    }

    @Override
    public void gotProvisioningUrl(String url) {
        Image image = QRGenerator.getImage(url);
        javafx.application.Platform.runLater(() -> loginPresenter.getQrImageView().setImage(image));
    }

    @Override
    public void gotProvisionMessage(String number) {
        int rnd = new Random().nextInt(1000);
        try {
            wave.createAccount(number, "gluon-"+rnd);
            login("YOU");
            wave.setMessageListener(this);
            wave.syncContacts();
            Platform.runLater(() -> loginPresenter.nextStep());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
