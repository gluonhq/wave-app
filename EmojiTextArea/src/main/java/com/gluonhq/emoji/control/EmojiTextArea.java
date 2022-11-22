package com.gluonhq.emoji.control;

import com.gluonhq.emoji.action.ActionFactory;
import com.gluonhq.emoji.impl.skin.EmojiTextAreaSkin;
import com.gluonhq.emoji.popup.EmojiSkinTone;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Skin;

public class EmojiTextArea extends Control {

    private EmojiTextAreaSkin.EmojiStyledTextArea textarea;

    private final ActionFactory actionFactory = new ActionFactory(this);

    public EmojiTextArea() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    // text
    private final StringProperty text = new SimpleStringProperty(this, "text");

    public final StringProperty textProperty() {
        return text;
    }

    public final String getText() {
        return text.get();
    }

    public final void setText(String value) {
        text.set(value);
    }

    // Puts the emoji button on the left or right side of the editor
    private final ObjectProperty<Side> emojiSide = new SimpleObjectProperty<>(this, "side", Side.LEFT);

    public final ObjectProperty<Side> emojiSideProperty() {
        return emojiSide;
    }

    public final Side getEmojiSide() {
        return emojiSide.get();
    }

    public final void setEmojiSide(Side value) {
        emojiSide.set(value);
    }

    // Defines the preferred skin tone
    private final ObjectProperty<EmojiSkinTone> skinToneProperty = new SimpleObjectProperty<>(this, "skinTone", EmojiSkinTone.NO_SKIN_TONE);

    public final ObjectProperty<EmojiSkinTone> skinToneProperty() {
        return skinToneProperty;
    }

    public final EmojiSkinTone getSkinTone() {
        return skinToneProperty.get();
    }

    public final void setSkinTone(EmojiSkinTone value) {
        skinToneProperty.set(value);
    }

    /**
     * Defines the action to be performed when enter is pressed
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>(this, "onAction");

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    public final void setOnAction(EventHandler<ActionEvent> value) {
        onAction.set(value);
    }

    public ActionFactory getActionFactory() {
        return actionFactory;
    }

    /**
     * The current position of the caret within the text.
     * The <code>anchor</code> and <code>caretPosition</code> make up the selection
     * range. Selection must always be specified in terms of begin &lt;= end, but
     * <code>anchor</code> may be less than, equal to, or greater than the
     * <code>caretPosition</code>. Depending on how the user selects text,
     * the caretPosition might represent the lower or upper bound of the selection.
     */
    private ReadOnlyIntegerWrapper caretPosition = new ReadOnlyIntegerWrapper(this, "caretPosition", 0);
    public final int getCaretPosition() { return caretPosition.get(); }
    public final ReadOnlyIntegerProperty caretPositionProperty() { return caretPosition.getReadOnlyProperty(); }


    public void clear() {
        if (!text.isBound()) {
            setText("");
        }
    }

    public void insertText(int position, String text) {
        if (textarea == null) return;
        textarea.insertText(position, text);
    }

    public void replaceText(IndexRange range, String text) {
        if (textarea == null) return;
        final int start = range.getStart();
        final int end = start + range.getLength();
        textarea.replaceText(start, end, text);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        EmojiTextAreaSkin emojiTextAreaSkin = new EmojiTextAreaSkin(this);
        textarea = emojiTextAreaSkin.getTextarea();
        if (textarea != null) {
            caretPosition.bind(textarea.caretPositionProperty());
        }
        return emojiTextAreaSkin;
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("emoji-text-area.css").toExternalForm();
    }

    public enum Side {
        LEFT,
        RIGHT;
    }

    // treat private
//    public void doSetFocused(boolean focused) {
//        this.setFocused(focused);
//    }

    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "emoji-text-area";

}


