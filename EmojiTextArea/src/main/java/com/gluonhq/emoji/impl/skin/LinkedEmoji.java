package com.gluonhq.emoji.impl.skin;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import javafx.scene.Node;
import org.fxmisc.richtext.model.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

public interface LinkedEmoji {

    boolean isReal();
    
    Optional<Emoji> getEmoji();

    String getEmojiData();

    Node createNode();

    Codec<LinkedEmoji> EMOJI_CODEC = new Codec<>() {

        @Override
        public String getName() {
            return "linkedEmoji";
        }

        @Override
        public void encode(DataOutputStream os, LinkedEmoji linkedEmoji) throws IOException {
            if (linkedEmoji.isReal()) {
                os.writeBoolean(true);
                Codec.STRING_CODEC.encode(os, linkedEmoji.getEmojiData());
            } else {
                os.writeBoolean(false);
            }
        }

        @Override
        public LinkedEmoji decode(DataInputStream is) throws IOException {
            if (is.readBoolean()) {
                String emojiData = Codec.STRING_CODEC.decode(is);
                return EmojiData.emojiFromCodepoints(emojiData)
                        .map(emoji -> ((LinkedEmoji) new RealLinkedEmoji(emoji)))
                        .orElse(new EmptyLinkedEmoji());
            } else {
                return new EmptyLinkedEmoji();
            }
        }
    };
}
