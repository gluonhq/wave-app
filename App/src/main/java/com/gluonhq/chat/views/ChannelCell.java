package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.chat.model.Channel;
import com.gluonhq.chat.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ChannelCell extends CharmListCell<Channel> {

    static final int AVATAR_SIZE = 64;
    
    private final ListTile tile;

    public ChannelCell() {
        tile = new ListTile();
        setGraphic(tile);
        setText(null);
        getStyleClass().add("channel-cell");
    }

    @Override
    public void updateItem(Channel channel, boolean empty) {
        super.updateItem(channel, empty);
        tile.setPrimaryGraphic(null);
        if (channel.getMembers().size() > 0) {
            User author = channel.getMembers().get(0);
            String avatarPath = author.getAvatarPath();
            if ((avatarPath != null) && (!avatarPath.isEmpty()) && !("null".equals(avatarPath))) {
                try {
                    byte[] b = Files.readAllBytes(Path.of(avatarPath));
                    ByteArrayInputStream bais = new ByteArrayInputStream(b);
                    Image im = new Image(bais, AVATAR_SIZE, AVATAR_SIZE, true, true);
                    ImageView iv = new ImageView(im);
                    tile.setPrimaryGraphic(iv);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
        /* TODO: Add User Image
        final Avatar avatar = new Avatar;
        avatar.setMouseTransparent(true);
        tile.setPrimaryGraphic(avatar);
        */

        tile.setTextLine(0, channel.displayName());
        if (channel.isHasUnread()) {
            System.err.println("UNREAD!");
            tile.setTextLine(0, "*" + channel.displayName()+"*");
            channel.setHasUnread(false);
        }
        tile.setOnMouseReleased(event -> {
            AppViewManager.CHAT_VIEW.getPresenter().ifPresent(presenter -> ((ChatPresenter) presenter).updateMessages(channel));
            // TODO: We want a better way to switch views if the screen size is <= 600
            if (tile.getScene().getWidth() <= 600) {
                AppViewManager.PORTRAIT_VIEW.switchView().ifPresent(p -> ((PortraitPresenter) p).loadChat());
            }
        });

    }
}
