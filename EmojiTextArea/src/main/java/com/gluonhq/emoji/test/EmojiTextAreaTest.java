package com.gluonhq.emoji.test;

import com.gluonhq.emoji.action.Action;
import com.gluonhq.emoji.control.EmojiTextArea;
import com.gluonhq.emoji.popup.EmojiSkinTone;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EmojiTextAreaTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        final BorderPane borderPane = new BorderPane();
        Button test = new Button("Test");
        final StackPane stackPane = new StackPane(test);
        stackPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(stackPane);
        EmojiTextArea textArea = new EmojiTextArea();
        textArea.setSkinTone(EmojiSkinTone.MEDIUM_SKIN_TONE);
        test.setOnAction(e -> textArea.setText("Text and \uD83D\uDE00"));
        borderPane.setBottom(textArea);
        final Scene scene = new Scene(borderPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        setupContextMenu(textArea);

    }

    private void setupContextMenu(EmojiTextArea textArea) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                createMenuItem("copy", textArea.getActionFactory().copy()),
                createMenuItem("cut", textArea.getActionFactory().cut()),
                createMenuItem("paste", textArea.getActionFactory().paste()),
                createMenuItem("delete", textArea.getActionFactory().delete()),
                new SeparatorMenuItem(),
                createMenuItem("undo", textArea.getActionFactory().undo()),
                createMenuItem("redo", textArea.getActionFactory().redo()),
                new SeparatorMenuItem(),
                createMenuItem("select all", textArea.getActionFactory().selectAll())
        );
        textArea.setContextMenu(contextMenu);
    }

    private static MenuItem createMenuItem(String item, Action action) {
        MenuItem menuItem = new MenuItem(item);
        menuItem.disableProperty().bind(action.disabledProperty());
        menuItem.setOnAction(action::execute);
        return menuItem;
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
