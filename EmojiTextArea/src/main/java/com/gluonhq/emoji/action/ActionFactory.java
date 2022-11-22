package com.gluonhq.emoji.action;

import com.gluonhq.emoji.control.EmojiTextArea;
import com.gluonhq.emoji.impl.skin.StyledTextAreaActionFactory;

public class ActionFactory {

    private static final StyledTextAreaActionFactory STYLED_TEXT_AREA_ACTION_FACTORY = new StyledTextAreaActionFactory();
    private final EmojiTextArea control;

    public ActionFactory(EmojiTextArea control) {
        this.control = control;
    }

    private Action copy;
    private Action cut;
    private Action paste;
    private Action delete;
    private Action undo;
    private Action redo;
    private Action selectAll;

    public Action copy() {
        if (copy == null) {
            copy = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.copy());
        }
        return copy;
    }

    public Action cut() {
        if (cut == null) {
            cut = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.cut());
        }
        return cut;
    }

    public Action paste() {
        if (paste == null) {
            paste = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.paste());
        }
        return paste;
    }

    public Action delete() {
        if (delete == null) {
            delete = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.delete());
        }
        return delete;
    }

    public Action undo() {
        if (undo == null) {
            undo = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.undo());
        }
        return undo;
    }

    public Action redo() {
        if (redo == null) {
            redo = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.redo());
        }
        return redo;
    }

    public Action selectAll() {
        if (selectAll == null) {
            selectAll = new BasicAction(control, action -> STYLED_TEXT_AREA_ACTION_FACTORY.selectAll());
        }
        return selectAll;
    }
}
