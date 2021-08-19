package com.gluonhq.chat.views;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.chat.GluonChat;
import com.gluonhq.chat.service.Service;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.ResourceBundle;

public class LoginPresenter extends GluonPresenter<GluonChat> {

    @FXML private View loginView;

    @FXML private TextField username;
    @FXML private Button button;
    @FXML private Pane top;

    @Inject private Service service;

    @FXML private ResourceBundle resources;

    public void initialize() {

        if (Platform.isIOS() && ! top.getStyleClass().contains("ios")) {
            top.getStyleClass().add("ios");
        }
        boolean notch = DisplayService.create().map(DisplayService::hasNotch).orElse(false);
        if (notch && !top.getStyleClass().contains("notch")) {
            top.getStyleClass().add("notch");
        }

        loginView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                getApp().getAppBar().setVisible(false);
                username.setDisable(true);
            }
        });

        loginView.setOnShown(e -> {
            top.requestFocus();
            username.setDisable(false);
        });

        if (Platform.isAndroid()) {
            username.setOnMouseClicked(e -> {
                top.requestFocus();
                username.requestFocus();
            });
        }
        username.setOnAction(e -> button.requestFocus());

        button.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        username.getText().isEmpty() || username.getText().length() < 2,
                        username.textProperty()));
        button.setOnAction(e -> {
            if (service.login(username.getText())) {
                AppViewManager.FIRST_VIEW.switchView(ViewStackPolicy.SKIP);
            } else {
                Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setContentText(resources.getString("message.name.unavailable"));
                alert.showAndWait();
            }
        });
    }
}
