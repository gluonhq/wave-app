package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.chat.model.Channel;
import com.gluonhq.chat.model.ChatMessage;
import com.gluonhq.chat.service.Service;
import javafx.beans.InvalidationListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.Observable;

public class ChannelPresenter {

    @FXML private TextField search;
    @FXML private CharmListView<Channel, String> channelList;
    @FXML private NotificationPane notificationPane;

    @Inject private Service service;
    @FXML private ResourceBundle resources;
    
    private FilteredList<Channel> channelFilteredList;

    public void initialize() {
        System.err.println("INIT CHANNELPRESENTER!!!!!!!!!!!!!!!\n\n\n\n\n\n\n");
        channelFilteredList = createChannelList();
        search.textProperty().addListener((o, ov, nv) -> channelFilteredList.setPredicate(channel -> {
            if (nv == null || nv.isEmpty()) {
                return true;
            }
            return channel.contains(nv);
        }));
        channelList.setItems(channelFilteredList);
        channelList.setCellFactory(param -> new ChannelCell());
        channelList.setHeadersFunction(param -> param.isDirect() ? "DIRECT" : "GROUP");
        channelList.setHeaderCellFactory(p -> new CharmListCell<>() {

            private final HBox box;
            private final Label label;
            {
                label = new Label();
                box = new HBox(label);
                box.getStyleClass().add("header-box");
            }

            @Override
            public void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    label.setText(resources.getString(item.isDirect() ? "channels.category.direct" : "channels.category.group"));
                    setGraphic(box);
                }
            }
        });
        notificationPane.setText("Update Available");
        notificationPane.getActions().addAll(new Action("Install", ae -> {
            service.installNewVersion();
            notificationPane.hide();
        }));
        service.newVersionAvailable().addListener((o, ov, nv) -> {
            if (nv) {
                notificationPane.show();
            }
        });
    }

    public void updateChannels(boolean removeSelection) {
        if (removeSelection) {
            channelList.setSelectedItem(null);
        }
        channelList.refresh();
    }

    private FilteredList<Channel> createChannelList() {
        SortedList<Channel> sortedList = new SortedList<>(service.getChannels());
        sortedList.setComparator(Comparator.comparing(this::latestMessageTime).reversed());
        for (Channel channel : sortedList) {
            channel.getMessages().addListener((InvalidationListener) o -> {
                sortedList.setComparator(Comparator.comparing(ChannelPresenter.this::latestMessageTime).reversed());
            });
        }
        System.err.println("[WS] created channellist sortedlist = "+Objects.hash(sortedList));
        sortedList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                System.err.println("[WS], sortedlist is now "+sortedList);
            }
        });
        return new FilteredList<>(sortedList);
    }

    private LocalDateTime latestMessageTime(Channel channel) {
        return channel.getMessages().stream()
                .map(ChatMessage::getTime)
                .max(LocalDateTime::compareTo)
                .orElseGet(() -> LocalDateTime.of(1970, 1, 1, 0, 0));
    }
}
