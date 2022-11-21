package com.gluonhq.emoji.impl.skin;

import com.gluonhq.emoji.Emoji;
import javafx.scene.Node;

import java.util.Optional;

public class EmptyLinkedEmoji implements LinkedEmoji {

    @Override
    public boolean isReal() {
        return false;
    }

    @Override
    public Optional<Emoji> getEmoji() {
        return Optional.empty();
    }

    @Override
    public String getEmojiData() {
        return "";
    }

    @Override
    public Node createNode() {
        throw new AssertionError("Unreachable code");
    }
}
