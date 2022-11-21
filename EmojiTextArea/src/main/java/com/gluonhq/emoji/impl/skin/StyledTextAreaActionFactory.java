package com.gluonhq.emoji.impl.skin;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public final class StyledTextAreaActionFactory {

    private final StyledTextAreaAction copy = new StyledTextAreaActionCopy();
    private final StyledTextAreaAction cut = new StyledTextAreaActionCut();
    private final StyledTextAreaAction paste = new StyledTextAreaActionPaste();
    private final StyledTextAreaAction delete = new StyledTextAreaActionDelete();
    private final StyledTextAreaAction undo = new StyledTextAreaActionUndo();
    private final StyledTextAreaAction redo = new StyledTextAreaActionRedo();
    private final StyledTextAreaAction selectAll = new StyledTextAreaActionSelectAll();


    public StyledTextAreaAction copy() {
        return copy;
    }

    public StyledTextAreaAction cut() {
        return cut;
    }

    public StyledTextAreaAction paste() {
        return paste;
    }

    public StyledTextAreaAction delete() {
        return delete;
    }

    public StyledTextAreaAction undo() {
        return undo;
    }

    public StyledTextAreaAction redo() {
        return redo;
    }

    public StyledTextAreaAction selectAll() {
        return selectAll;
    }

    private static class StyledTextAreaActionCopy implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            textArea.copy(); // selected string + format, so it works only for pasting internally.
            ClipboardContent content = new ClipboardContent();
            Clipboard.getSystemClipboard().getContentTypes()
                    .forEach(df -> content.put(df, Clipboard.getSystemClipboard().getContent(df)));
            content.putString(textArea.getSelectedContentAsText());
            Clipboard.getSystemClipboard().setContent(content);
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(
                    () -> textArea.getSelection() == null || textArea.getSelection().getLength() == 0 , textArea.selectionProperty());
        }
    }

    private static class StyledTextAreaActionCut implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            textArea.copy(); // selected string + format, so it works only for pasting internally.
            ClipboardContent content = new ClipboardContent();
            Clipboard.getSystemClipboard().getContentTypes()
                    .forEach(df -> content.put(df, Clipboard.getSystemClipboard().getContent(df)));
            content.putString(textArea.getSelectedContentAsText());
            IndexRange selection = textArea.getSelection();
            textArea.deleteText(selection.getStart(), selection.getEnd());
            Clipboard.getSystemClipboard().setContent(content);
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(
                    () -> textArea.getSelection() == null || textArea.getSelection().getLength() == 0 , textArea.selectionProperty());
        }
    }

    private static class StyledTextAreaActionPaste implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            if (!textArea.pasteStringAsRichContent()) {
                // built-in paste
                textArea.paste();
            }
        }
    }

    private static class StyledTextAreaActionDelete implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            IndexRange selection = textArea.getSelection();
            textArea.deleteText(selection.getStart(), selection.getEnd());
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(
                    () -> textArea.getSelection() == null || textArea.getSelection().getLength() == 0 , textArea.selectionProperty());
        }
    }

    private static class StyledTextAreaActionUndo implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            textArea.undo();
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(() -> !textArea.getUndoManager().isUndoAvailable(), textArea.getUndoManager().nextUndoProperty());
        }
    }

    private static class StyledTextAreaActionRedo implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            textArea.redo();
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(() -> !textArea.getUndoManager().isRedoAvailable(), textArea.getUndoManager().nextRedoProperty());
        }
    }

    private static class StyledTextAreaActionSelectAll implements StyledTextAreaAction {

        @Override
        public void apply(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            textArea.selectAll();
        }

        @Override
        public BooleanBinding getDisabledBinding(EmojiTextAreaSkin.EmojiStyledTextArea textArea) {
            return Bindings.createBooleanBinding(() -> textArea.getText().isEmpty(), textArea.textProperty());
        }
    }
}
