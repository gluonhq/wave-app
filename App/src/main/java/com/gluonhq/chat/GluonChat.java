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
        bindProvider(Service.class, CloudlinkService::new);

        Injector.setInstanceSupplier(this);
    }};

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.RED.assignTo(scene);

        /*String deviceFactorSuffix = Services.get(DeviceService.class)
                .map(s -> {
                    switch (s.getPlatform()) {
                        case "Android":
                            return "android";
                    }
                    return "";
                })
                .orElse("");

        String formFactorSuffix = Services.get(DisplayService.class)
                .map(s -> s.isTablet() ? "_tablet" : "")
                .orElse("");

        String stylesheetName = String.format("style_%s%s%s.css",
                Platform.getCurrent().name().toLowerCase(Locale.ROOT),
                deviceFactorSuffix,
                formFactorSuffix);
        scene.getStylesheets().add(GluonChat.class.getResource("/"+ stylesheetName).toExternalForm());*/
        
        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            final String android_stylesheet = GluonChat.class.getResource("/styles_android.css").toExternalForm();
            if (newValue.doubleValue() > 400) {
                scene.getStylesheets().remove(android_stylesheet);
            } else {
                scene.getStylesheets().add(android_stylesheet);
            }
        });*/
        scene.getStylesheets().add(GluonChat.class.getResource("/styles.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(GluonChat.class.getResourceAsStream("/icon.png")));

        scene.getWindow().setOnCloseRequest(e ->
                LifecycleService.create().ifPresent(LifecycleService::shutdown));

        //ScenicView.show(scene);
    }

    public static void main(String[] args) {
//        System.setProperty("charm-desktop-form", "tablet");
        launch(args);
    }
}