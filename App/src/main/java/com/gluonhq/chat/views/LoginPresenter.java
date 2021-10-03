package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.chat.GluonChat;
import com.gluonhq.chat.service.Service;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;


import javax.inject.Inject;
import java.util.ResourceBundle;

public class LoginPresenter extends GluonPresenter<GluonChat> {

    @FXML private View loginView;
    @FXML private ImageView qrImageView;

    @Inject private Service service;

    @FXML private ResourceBundle resources;

    public void initialize() {
        loginView.setOnShowing(e -> {
            getApp().getAppBar().setVisible(false);
            getApp().getAppBar().setManaged(false);
        });
        if (service.loggedUser() != null) {
            nextStep();
        } else {
            service.bootstrap(this);
        }
    }

    public ImageView getQrImageView() {
        return qrImageView;
    }

    public void nextStep() {
        AppViewManager.FIRST_VIEW.switchView(ViewStackPolicy.SKIP);
    }
}
