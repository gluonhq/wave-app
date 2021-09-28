package com.gluonhq.chat.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.chat.GluonChat;
import com.gluonhq.chat.service.Service;
import com.gluonhq.elita.QRGenerator;
import com.gluonhq.wave.WaveManager;
import com.gluonhq.wave.provisioning.ProvisioningClient;
import java.security.Security;
import java.util.Random;
import javafx.fxml.FXML;

import javax.inject.Inject;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class LoginPresenter extends GluonPresenter<GluonChat> implements ProvisioningClient {

    @FXML
    private View loginView;

    @FXML
    private ImageView qrImageView;
            
    @Inject private Service service;
    
    private WaveManager wave = WaveManager.getInstance();

    static {
        Security.addProvider(new BouncyCastleProvider());
        Security.setProperty("crypto.policy", "unlimited");
    }

    @Override
    public void gotProvisioningUrl(String url) {
        System.err.println("[LoginPresenter] setProvisioningURL to " + url);
        Image image = QRGenerator.getImage(url);
        javafx.application.Platform.runLater(() -> qrImageView.setImage(image));
    }

    @Override
    public void gotProvisionMessage(String number) {
        System.err.println("Got provisioningMessage " + number);
        int rnd = new Random().nextInt(1000);
        try {
            wave.createAccount(number, "gluon-"+rnd);
            wave.syncContacts();
            Platform.runLater(() -> AppViewManager.FIRST_VIEW.switchView(ViewStackPolicy.SKIP));

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
