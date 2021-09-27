package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.chat.GluonChat;
import com.gluonhq.chat.service.Service;
import com.gluonhq.elita.QRGenerator;
import com.gluonhq.wave.WaveManager;
import com.gluonhq.wave.provisioning.ProvisioningClient;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;

import javax.inject.Inject;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;

public class LoginPresenter extends GluonPresenter<GluonChat> implements ProvisioningClient {

    @FXML
    private View loginView;

    @Inject private Service service;

    @FXML
    private ResourceBundle resources;
    
    private WaveManager wave = new WaveManager();

    static {
        Security.addProvider(new BouncyCastleProvider());
        Security.setProperty("crypto.policy", "unlimited");
    }

    @Override
    public void gotProvisioningUrl(String url) {
        System.err.println("[LoginPresenter] setProvisioningURL to " + url);
        Image image = QRGenerator.getImage(url);
        javafx.application.Platform.runLater(()
                -> loginView.getChildren().add(new ImageView(image)));
    }

    @Override
    public void gotProvisionMessage(String number) {
        System.err.println("Got provisioningMessage " + number);
        int rnd = new Random().nextInt(1000);
        try {
            wave.createAccount(number, "gluon-"+rnd);
            wave.syncContacts();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initialize() throws InterruptedException {
        loginView.setOnShowing(e -> {
            getApp().getAppBar().setVisible(false);
            getApp().getAppBar().setManaged(false);
        });
        if (service.loggedUser() != null) {
            AppViewManager.FIRST_VIEW.switchView(ViewStackPolicy.SKIP);
        } else {
            Runnable r = () -> wave.startProvisioning(this);
            Thread t = new Thread(r);
            t.start();
        }
    }
    
}
