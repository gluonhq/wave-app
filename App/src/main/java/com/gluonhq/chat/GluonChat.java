package com.gluonhq.chat;

import com.airhacks.afterburner.injection.Injector;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.charm.glisten.afterburner.GluonInstanceProvider;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.gluonhq.chat.service.CloudlinkService;
import com.gluonhq.chat.service.DummyService;
import com.gluonhq.chat.service.Service;
import com.gluonhq.chat.views.AppViewManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GluonChat extends MobileApplication {

    private static final Logger LOG = Logger.getLogger(GluonChat.class.getName());
    static {
        try {
            LogManager.getLogManager().readConfiguration(GluonChat.class.getResourceAsStream("/logging.properties"));

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Storage Service Error", e);
        }
    }

    private static final GluonInstanceProvider instanceSupplier = new GluonInstanceProvider() {{
        // bindProvider(Service.class, CloudlinkService::new);
        bindProvider(Service.class, DummyService::new);

        Injector.setInstanceSupplier(this);
    }};

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.RED.assignTo(scene);

        scene.getStylesheets().add(GluonChat.class.getResource("/styles.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(GluonChat.class.getResourceAsStream("/icon.png")));

        scene.getWindow().setOnCloseRequest(e ->
                LifecycleService.create().ifPresent(LifecycleService::shutdown));

        // TODO: Remove
        //ScenicView.show(scene);
    }

    public static void main(String[] args) {
//        System.setProperty("charm-desktop-form", "tablet");
        launch(args);
    }
}