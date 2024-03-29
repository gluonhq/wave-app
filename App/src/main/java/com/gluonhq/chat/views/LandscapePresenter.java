package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;

import java.util.ResourceBundle;

public class LandscapePresenter {

    @FXML private View landscapeView;

    @FXML private ResourceBundle resources;

    public void initialize() {
        landscapeView.showingProperty().addListener((obs, ov, nv) -> {
            AppBar appBar = AppManager.getInstance().getAppBar();
            if (nv) {
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(/*e ->
                        AppManager.getInstance().getDrawer().open()*/));
                appBar.setTitleText(resources.getString("landscape.view.title"));
                // TODO: Duplicate code
                ToggleButton theme = new ToggleButton();
                theme.getStyleClass().addAll("icon-toggle");
                theme.setGraphic(MaterialDesignIcon.LIGHTBULB_OUTLINE.graphic());
                theme.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                theme.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    final String darkStyleSheet = LandscapePresenter.class.getResource("/styles_dark.css").toExternalForm();
                    if (newValue) {
                        landscapeView.getScene().getStylesheets().add(darkStyleSheet);
                    } else {
                        landscapeView.getScene().getStylesheets().remove(darkStyleSheet);
                    }
                });
                appBar.getActionItems().add(theme);
            }
        });
    }

    void loadLandscapeView() {
        AppManager.getInstance().retrieveView(AppViewManager.CHAT_VIEW.getId())
                .ifPresentOrElse(landscapeView::setCenter,
                        () -> System.out.println("Error finding CHAT_VIEW"));

        AppManager.getInstance().retrieveView(AppViewManager.CHANNEL_VIEW.getId())
                .ifPresentOrElse(landscapeView::setLeft,
                        () -> System.out.println("Error finding USERS_VIEW"));
    }

}
