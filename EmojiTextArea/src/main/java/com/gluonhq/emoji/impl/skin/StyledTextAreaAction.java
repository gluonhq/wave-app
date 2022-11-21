package com.gluonhq.emoji.impl.skin;

import javafx.beans.binding.BooleanBinding;

public interface StyledTextAreaAction {
    void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea);

    default BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
        return null;
    }
}
