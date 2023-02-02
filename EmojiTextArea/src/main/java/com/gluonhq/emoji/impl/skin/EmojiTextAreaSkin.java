package com.gluonhq.emoji.impl.skin;

//import com.gluonhq.attach.keyboard.KeyboardService;
import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.control.EmojiTextArea;
import com.gluonhq.emoji.event.EmojiEvent;
import com.gluonhq.emoji.popup.EmojiPopOver;
import com.gluonhq.emoji.util.TextUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.controlsfx.control.PopOver;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledSegment;
import org.fxmisc.richtext.util.UndoUtils;
import org.reactfx.util.Either;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import java.text.BreakIterator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gluonhq.emoji.control.EmojiTextArea.Side.LEFT;

public class EmojiTextAreaSkin extends SkinBase<EmojiTextArea> {

    private final PopOver popOver;
    private final Button emojiButton;
    private final EmojiSuggestion emojiSuggestion;
    private final EmojiStyledTextArea textarea;
    private final VirtualizedScrollPane<EmojiStyledTextArea> virtualizedScrollPane;

    private final StyleableDoubleProperty spacing = new SimpleStyleableDoubleProperty(SPACING, this, "spacing", 0.0);

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    public EmojiTextAreaSkin(EmojiTextArea control) {
        super(control);
        textarea = new EmojiStyledTextArea();
        updateTextArea(control.getText());
        textarea.setWrapText(true);
        virtualizedScrollPane = new VirtualizedScrollPane<>(textarea) {
            @Override
            public Val<Double> totalHeightEstimateProperty() {
                return Var.newSimpleVar(
                        getContent().getPadding().getTop() + 
                        super.totalHeightEstimateProperty().getOrElse(0.0) + 
                        getContent().getPadding().getBottom()
                );
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                getContent().requestLayout();
            }

            @Override
            protected double computeMinWidth(double height) {
                return super.computeMinWidth(height) + getContent().computeMinWidth(height);
            }
        };

        emojiSuggestion = new EmojiSuggestion();
        emojiSuggestion.setOnAction(emojiEvent -> {
            final Emoji emoji = emojiEvent.getEmoji();
            final IndexRange wordIndex = findWordIndexAtCaret();
            if (!(wordIndex.getStart() == 0 && wordIndex.getEnd() == 0)) {
                textarea.replace(wordIndex.getStart(), wordIndex.getEnd(), emoji);
                emojiSuggestion.hide();
            }
        });

        popOver = getSkinnable().getEmojiPopOver();
        getSkinnable().emojiPopOverProperty().addListener((obs, ov, nv) -> setupPopover());
        setupPopover();

        updateSide();
        getSkinnable().emojiSideProperty().addListener(o -> updateSide());

        emojiButton = new Button(EmojiData.emojiForText("smile"));
        emojiButton.getStyleClass().add("emoji-button");
        emojiButton.setOnAction(e -> popOver.show(emojiButton));
        getChildren().addAll(virtualizedScrollPane, emojiButton, emojiSuggestion);

        final Region region = new Region();
        region.getStyleClass().add("icon");
        emojiButton.setGraphic(region);
        emojiButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        spacing.addListener(o -> control.requestLayout());

        BooleanProperty isReplacing = new SimpleBooleanProperty();
        textarea.multiRichChanges().successionEnds(Duration.ofMillis(100)).subscribe(change -> {
            if (!isReplacing.get()) {
                isReplacing.set(true);
                control.setText(textarea.getContentAsText());
                isReplacing.set(false);
            }
        });
        control.textProperty().addListener((obs, ov, nv) -> {
            if (!isReplacing.get()) {
                isReplacing.set(true);
                updateTextArea(nv);
                isReplacing.set(false);
            }
        });
        control.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                textarea.requestFocus();
            }
        });

        textarea.contextMenuObjectProperty().bind(control.contextMenuProperty());

//        double fontSize = ((Region) textarea.lookup(".styled-text-area")).getChildrenUnmodifiable().stream()
//                .filter(Text.class::isInstance)
//                .findFirst()
//                .map(Text.class::cast)
//                .map(t -> t.getFont().getSize())
//                .orElse(12.0);
//        Color front = ((Region) textarea.lookup(".styled-text-area")).getChildrenUnmodifiable().stream()
//                .filter(Text.class::isInstance)
//                .findFirst()
//                .map(Text.class::cast)
//                .map(Text::getFill)
//                .map(Color.class::cast)
//                .orElse(Color.BLACK);
//
//        Color back = textarea.getBackground().getFills().stream()
//                .map(BackgroundFill::getFill)
//                .filter(Color.class::isInstance)
//                .findFirst()
//                .map(Color.class::cast)
//                .orElse(Color.WHITE);

//        KeyboardService.create().ifPresent(k ->
//                k.forNode(textarea, control.textProperty(), fontSize, front, back));

    }

    public EmojiStyledTextArea getTextarea() {
        return textarea;
    }

    private void updateTextArea(String text) {
        textarea.clear();
        if (text == null) return; // clear the textarea when null is passed?
        TextUtils.convertToStringAndEmojiObjects(text).forEach(o -> {
            if (o instanceof String) {
                textarea.appendText((String) o);
            } else {
                textarea.replaceSelection(getStyledDocumentFromEmoji((Emoji) o));
            }
        });
    }

    private ReadOnlyStyledDocument<ParStyle, Either<String, LinkedEmoji>, TextStyle> getStyledDocumentFromEmoji(Emoji emoji) {
        return ReadOnlyStyledDocument.fromSegment(
                        Either.right(new RealLinkedEmoji(emoji)),
                        ParStyle.EMPTY, TextStyle.EMPTY, textarea.getSegOps());
    }

    private void updateSide() {
        if (getSkinnable().getEmojiSide() == LEFT) {
            popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
        } else {
            popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        }
    }

    private void setupPopover() {
        if (popOver instanceof EmojiPopOver) {
            EmojiPopOver emojiPopOver = (EmojiPopOver) popOver;
            emojiPopOver.setOnAction(e -> onEmojiAction((Emoji) e.getSource()));
            emojiPopOver.skinToneProperty().unbind();
            emojiPopOver.setSkinTone(getSkinnable().getSkinTone());
            getSkinnable().skinToneProperty().bindBidirectional(emojiPopOver.skinToneProperty());
        } else {
            popOver.addEventHandler(EmojiEvent.ANY, e -> onEmojiAction(((EmojiEvent) e).getEmoji()));
        }
    }

    private void onEmojiAction(Emoji emoji) {
        textarea.replaceSelection(getStyledDocumentFromEmoji(emoji));
        textarea.requestFocus();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {

        final double buttonHeight = emojiButton.prefHeight(-1);
        final double buttonWidth = emojiButton.prefWidth(buttonHeight);

        emojiButton.resizeRelocate(
                getSkinnable().getEmojiSide() == LEFT ? contentX : contentX + contentWidth - buttonWidth,
                snappedTopInset(),
                buttonWidth,
                buttonHeight
        );

        virtualizedScrollPane.resizeRelocate(
                getSkinnable().getEmojiSide() == LEFT ? contentX + buttonWidth + spacing.get() : contentX,
                contentY,
                contentWidth - buttonWidth - spacing.get(),
                contentHeight
        );

        final double emojiSuggestionWidth = contentWidth - buttonWidth - spacing.get();
        final double emojiSuggestionHeight = emojiSuggestion.prefHeight(emojiSuggestionWidth);
        emojiSuggestion.resizeRelocate(
                getSkinnable().getEmojiSide() == LEFT ? contentX + buttonWidth + spacing.get() : contentX, 
                contentY - emojiSuggestionHeight, 
                emojiSuggestionWidth, 
                emojiSuggestionHeight
        );
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + virtualizedScrollPane.minWidth(height) + emojiButton.minWidth(-1) + rightInset;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + virtualizedScrollPane.prefWidth(height) + emojiButton.prefWidth(-1) + rightInset;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + emojiButton.prefHeight(-1) + bottomInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + virtualizedScrollPane.prefHeight(width) + bottomInset;
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().getScene().getWindow().getHeight() / 2;
    }

    private static Node createNode(StyledSegment<Either<String, LinkedEmoji>, TextStyle> seg, BiConsumer<? super TextExt, TextStyle> applyStyle) {
        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedEmoji::createNode);
    }

    /**
     * Returns the IndexRange of the word where caret is currently positioned
     * @return IndexRange of word where caret is currently positioned
     */
    private IndexRange findWordIndexAtCaret() {
        final int caretPosition = textarea.getCaretPosition();
        Pattern pattern = Pattern.compile("\\S+");
        Matcher matcher = pattern.matcher(textarea.getText());
        while (matcher.find()) {
            if (matcher.start() <= caretPosition && matcher.end() >= caretPosition) {
                return new IndexRange(matcher.start(), matcher.end());
            }
        }
        return IndexRange.valueOf("0, 0");
    }

    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    private static final CssMetaData<EmojiTextArea, Number> SPACING =
            new CssMetaData<>("-fx-spacing",
                    SizeConverter.getInstance(), 0.0) {

                @Override
                public boolean isSettable(EmojiTextArea node) {
                    final EmojiTextAreaSkin skin = (EmojiTextAreaSkin) node.getSkin();
                    return !skin.spacing.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(EmojiTextArea node) {
                    final EmojiTextAreaSkin skin = (EmojiTextAreaSkin) node.getSkin();
                    return skin.spacing;
                }
            };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
    static {
        final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
        styleables.add(SPACING);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    /**
     * {@inheritDoc}
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /***************************************************************************
     *                                                                         *
     *                         Inner Class                                     *
     *                                                                         *
     **************************************************************************/

    public class EmojiStyledTextArea extends GenericStyledArea<ParStyle, Either<String, LinkedEmoji>, TextStyle> {

        private static final int LINE_SIZE = 22;
        private static final int MIN_WIDTH = 250;

        private final KeyCodeCombination OPTION_DEL_KEY_COMBINATION = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.ALT_DOWN);
        private final KeyCodeCombination COMMAND_DEL_KEY_COMBINATION = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN);
        private final KeyCodeCombination OPTION_RIGHT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
        private final KeyCodeCombination SHIFT_OPTION_RIGHT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN);
        private final KeyCodeCombination OPTION_LEFT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
        private final KeyCodeCombination SHIFT_OPTION_LEFT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN);
        private final KeyCodeCombination COMMAND_RIGHT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN);
        private final KeyCodeCombination SHIFT_COMMAND_RIGHT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
        private final KeyCodeCombination COMMAND_LEFT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN);
        private final KeyCodeCombination SHIFT_COMMAND_LEFT_KEY_COMBINATION = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);

        private final DataFormat richDataFormat;

        EmojiStyledTextArea() {
            super(ParStyle.EMPTY,
                    (paragraph, style) -> paragraph.setStyle(style.toCss()),
                    TextStyle.EMPTY,
                    SegmentOps.<TextStyle>styledTextOps()._or(new LinkedEmojiOps<>(), (s1, s2) -> Optional.empty()),
                    seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));

            addBehaviorChanges();

            Codec<StyledSegment<Either<String, LinkedEmoji>, TextStyle>> styledSegCodec = Codec.styledSegmentCodec(Codec.eitherCodec(Codec.STRING_CODEC, LinkedEmoji.EMOJI_CODEC), TextStyle.CODEC);
            setStyleCodecs(ParStyle.CODEC, styledSegCodec);

            Codec<StyledDocument<ParStyle, Either<String, LinkedEmoji>, TextStyle>> codec = ReadOnlyStyledDocument.codec(ParStyle.CODEC, styledSegCodec, getSegOps());
            richDataFormat = DataFormat.lookupMimeType(codec.getName()) != null ?
                    DataFormat.lookupMimeType(codec.getName()) : new DataFormat(codec.getName());

            setUndoManager(UndoUtils.richTextUndoManager(this));
        }

        @Override
        protected double computeMinWidth(double height) {
            return snappedLeftInset() + MIN_WIDTH + snappedRightInset();
        }

        @Override
        protected double computePrefHeight(double width) {
            int noOfLines = 0;
            int paragraphIndex = 0;
            while (paragraphIndex < getParagraphs().size()) {
                noOfLines += getParagraphLinesCount(paragraphIndex++);
            }
            return snappedTopInset() + noOfLines * LINE_SIZE + snappedBottomInset();
        }

        private void addBehaviorChanges() {
            addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                // TODO: Find a better way
                if (emojiSuggestion.isVisible()) {
                    switch (e.getCode()) {
                        case ENTER:
                        case TAB:
                        case RIGHT:
                        case LEFT:
                        case UP:
                        case DOWN:
                        case SHIFT:
                            emojiSuggestion.fireEvent(e);
                            e.consume();
                            requestFocus();
                            return;
                    }
                }
                if (new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN).match(e) ||
                        new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN).match(e)) {
                    replaceSelection("\n");
                    requestFollowCaret();
                    e.consume();
                } else if (e.getCode() == KeyCode.ENTER) {
                    if (getSkinnable().getOnAction() != null) {
                        getSkinnable().getOnAction().handle(new ActionEvent());
                    }
                    e.consume();
                }
                if (System.getProperty("os.name").startsWith("Mac")) {
                    if (COMMAND_DEL_KEY_COMBINATION.match(e)) {
                        updateTextArea(textarea.getText().substring(getCaretPosition()));
                        textarea.moveTo(0);
                        e.consume();
                    } else if (OPTION_DEL_KEY_COMBINATION.match(e)) {
                        int caretPosition = getCaretPosition();
                        String text = textarea.getText().substring(0, caretPosition).trim();
                        int indexOf = Math.max(text.lastIndexOf(" ") + 1, 0);
                        updateTextArea(text.substring(0, indexOf) + textarea.getText().substring(caretPosition));
                        textarea.moveTo(indexOf, SelectionPolicy.CLEAR);
                        e.consume();
                    } else if (OPTION_RIGHT_KEY_COMBINATION.match(e) || SHIFT_OPTION_RIGHT_KEY_COMBINATION.match(e) ) {
                        int caretPosition = getCaretPosition();
                        if (caretPosition == textarea.getText().length()) return;
                        if (textarea.getText().charAt(caretPosition) == ' ') {
                            caretPosition++;
                        }
                        int indexOfNextWhitespace = textarea.getText().indexOf(" ", caretPosition);
                        if (indexOfNextWhitespace == -1) {
                            indexOfNextWhitespace = textarea.getText().length();
                        }
                        textarea.moveTo(indexOfNextWhitespace, e.isShiftDown() ? SelectionPolicy.ADJUST : SelectionPolicy.CLEAR);
                        e.consume();
                    } else if (OPTION_LEFT_KEY_COMBINATION.match(e) || SHIFT_OPTION_LEFT_KEY_COMBINATION.match(e)) {
                        final int caretPosition = getCaretPosition();
                        String substring = textarea.getText().substring(0, caretPosition).trim();
                        int indexOfPreviousWhitespace = substring.lastIndexOf(" ") + 1;
                        textarea.moveTo(indexOfPreviousWhitespace, e.isShiftDown() ? SelectionPolicy.ADJUST : SelectionPolicy.CLEAR);
                        e.consume();
                    } else if (COMMAND_RIGHT_KEY_COMBINATION.match(e) || SHIFT_COMMAND_RIGHT_KEY_COMBINATION.match(e)) {
                        textarea.moveTo(textarea.getText().length(), e.isShiftDown() ? SelectionPolicy.ADJUST : SelectionPolicy.CLEAR);
                        e.consume();
                    } else if (COMMAND_LEFT_KEY_COMBINATION.match(e) || SHIFT_COMMAND_LEFT_KEY_COMBINATION.match(e)) {
                        textarea.moveTo(0, e.isShiftDown() ? SelectionPolicy.ADJUST : SelectionPolicy.CLEAR);
                        e.consume();
                    }
                }
            });

            addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                // Need a delay for the text property to be updated
                Platform.runLater(() -> checkAndShowPopup(e));
            });

            addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (e.getClickCount() == 2) {
                    selectWord();
                    e.consume();
                }
            });

            addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if ((e.getCode() == KeyCode.V && e.isShortcutDown()) ||
                    (e.getCode() == KeyCode.INSERT && e.isShiftDown()) ||
                    e.getCode() == KeyCode.PASTE) {
                    if (pasteStringAsRichContent()) {
                        // don't forward event, it was already pasted as rich content
                        e.consume();
                    }
                }
            });
        }

        public void selectWord() {
            BreakIterator breakIterator = BreakIterator.getWordInstance();
            breakIterator.setText(getText());

            int start = calculatePositionViaBreakingBackwards(1, breakIterator, getCaretPosition());
            int end = calculatePositionViaBreakingForwards(1, breakIterator, getCaretPosition());
            selectRange(start, end);
        }

        /**
         * Export Either<String, LinkedEmoji> as text
         * @return a string with plain text and/or unicode from emojis
         */
        String getContentAsText() {
            return getContentAsText(getParagraphs());
        }

        String getSelectedContentAsText() {
            IndexRange sel = this.getSelection();
            return getContentAsText(subDocument(sel.getStart(), sel.getEnd()).getParagraphs());
        }

        private String getContentAsText(List<Paragraph<ParStyle, Either<String, LinkedEmoji>, TextStyle>> paragraphs) {
            StringBuilder text = new StringBuilder();
            for (int index = 0; index < paragraphs.size(); index++) {
                if (index > 0) {
                    text.append("\n");
                }
                paragraphs.get(index).getSegments().stream()
                        .map(segment -> segment.isLeft() ? segment.getLeft() :
                                segment.getRight().getEmoji().map(Emoji::character).orElse(""))
                        .forEach(text::append);
            }
            return text.toString();
        }

        boolean pasteStringAsRichContent() {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            // If clipboard contains richDataFormat, just paste with the built-in action,
            // else convert the string into Either<String, LinkedEmoji>
            if (!clipboard.hasContent(richDataFormat)) {
                if (clipboard.hasString()) {
                    String text = clipboard.getString();
                    if (text != null) {
                        replaceSelection("");
                        TextUtils.convertToStringAndEmojiObjects(text).forEach(o -> {
                            if (o instanceof String) {
                                insertText(getCaretPosition(), (String) o);
                            } else {
                                insert(getCaretPosition(), getStyledDocumentFromEmoji((Emoji) o));
                            }
                        });
                    }
                }
                return true;
            }
            return false;
        }

        /**
         * Replaces a range with emoji, adds a whitespace after emoji
         */
        private void replace(int start, int end, Emoji emoji) {
            replace(start, end, getStyledDocumentFromEmoji(emoji));
            textarea.insertText(textarea.getCaretPosition(), " ");
        }

        private void checkAndShowPopup(KeyEvent e) {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.SPACE) {
                emojiSuggestion.hide();
            } else {
                final String emojiWord = findEmojiWordAtCaret();
                if (!emojiWord.isEmpty()) {
                    final List<Emoji> emojis = EmojiData.search(emojiWord.substring(1));
                    if (emojis.isEmpty()) {
                        emojiSuggestion.hide();
                    } else {
                        emojiSuggestion.getEmojis().setAll(emojis);
                        if (!emojiSuggestion.isVisible()) {
                            emojiSuggestion.show();
                        }
                    }
                } else {
                    emojiSuggestion.hide();
                }
            }
        }

        // Finds the word in the given string at the given index and checks if it resembles an emoji
        // If the index is greater than the length of the string, it will return empty
        private String findEmojiWordAtCaret() {
            Pattern pattern = Pattern.compile(":[\\w-]{3,}");
            Matcher matcher = pattern.matcher(getText());
            while (matcher.find()) {
                final int caretPosition = getCaretPosition();
                if (matcher.start() <= caretPosition && matcher.end() >= caretPosition) {
                    return matcher.group();
                }
            }
            return "";
        }

        /** Assumes that {@code getArea().getLength != 0} is true and {@link BreakIterator#setText(String)} has been called */
        private int calculatePositionViaBreakingForwards(int numOfBreaks, BreakIterator breakIterator, int position) {
            breakIterator.following(position);
            for (int i = 1; i < numOfBreaks; i++) {
                breakIterator.next(numOfBreaks);
            }
            return breakIterator.current();
        }

        /** Assumes that {@code getArea().getLength != 0} is true and {@link BreakIterator#setText(String)} has been called */
        private int calculatePositionViaBreakingBackwards(int numOfBreaks, BreakIterator breakIterator, int position) {
            breakIterator.preceding(position);
            for (int i = 1; i < numOfBreaks; i++) {
                breakIterator.previous();
            }
            return breakIterator.current();
        }

    }
}
