package com.gluonhq.chat.views;

//import com.gluonhq.attach.orientation.OrientationService;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.chat.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

import static com.gluonhq.chat.GluonChat.VIEW_CHANGE_WIDTH;

public class HomePresenter {

    @FXML private View homeView;
    @Inject private Service service;
    
    private final ChangeListener<Number> widthListener = (o, ov, nv) -> changeOrientation(nv.doubleValue());
    private final ChangeListener<Boolean> showingListener = (obs, ov, nv) -> {
        if (nv) {
            homeView.getScene().widthProperty().addListener(widthListener);
        } else {
            homeView.getScene().widthProperty().removeListener(widthListener);
        }
    };

    public void initialize() {

//        OrientationService.create().ifPresent(o -> o.orientationProperty().addListener(obs -> setupView()));
        setupView();
        homeView.showingProperty().addListener(showingListener);
    }

    private void setupView() {
        if (service.loggedUser() == null) {
            AppViewManager.LOGIN_VIEW.switchView(ViewStackPolicy.SKIP);
        } else {
            loadContacts();
        }
    }

    private void changeOrientation(double width) {
        if (width > VIEW_CHANGE_WIDTH) {
            AppViewManager.LANDSCAPE_VIEW.switchView()
                    .ifPresent(p -> ((LandscapePresenter) p).loadLandscapeView());
        } else {
            AppViewManager.PORTRAIT_VIEW.switchView()
                    .ifPresent(p -> ((PortraitPresenter) p).loadChat());
        }
    }

    private void loadContacts() {
        Task<Void> channelsTask = new Task<>() {
            @Override
            protected Void call() {
                // service.getChannels();
                service.initializeService();
                return null;
            }
        };
        channelsTask.setOnSucceeded(e -> {
            if (homeView.isShowing()) {
                final double width = homeView.getScene().getWidth();
                changeOrientation(width);
            }
        });
        Thread th = new Thread(channelsTask);
        th.setDaemon(true);
        th.start();
        final VBox vBox = new VBox(10, new ProgressIndicator(), new Label("Retrieving contacts.."));
        vBox.setAlignment(Pos.CENTER);
        homeView.setCenter(new StackPane(vBox));
    }
}
