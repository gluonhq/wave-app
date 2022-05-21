package com.gluonhq.emoji;

import org.junit.jupiter.api.Test;

import static com.gluonhq.emoji.EmojiData.emojiFromUnicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static com.gluonhq.emoji.EmojiData.emojiForText;
import static com.gluonhq.emoji.EmojiData.emojiFromCodeName;

public class EmojiDataTest {

    @Test
    public void emojiForTextKnown() {
        assertEquals("😄", emojiForText("smile"));
    }

    @Test
    public void emojiForTextUnknown() {
        assertEquals("unknown", emojiForText("unknown"));
    }

    @Test
    public void emojiForTextStripForKnown() {
        assertEquals("😄", emojiForText("smile", true));
    }

    @Test
    public void emojiForTextStripForUnknown() {
        assertEquals("", emojiForText("unknown", true));
    }

    @Test
    public void emojiForColonTextTest() {
        assertNotNull(emojiFromCodeName(":smile:"));
        assertNotNull(emojiFromCodeName(":kissing:"));
    }

    @Test
    public void emojiFoUnicode() {
        assertNotNull(emojiFromUnicode("1F44B"));
    }

    @Test
    public void emojiForSkinTone() {
        assertNotNull(emojiFromUnicode("1F44B-1F3FC"));
    }
}