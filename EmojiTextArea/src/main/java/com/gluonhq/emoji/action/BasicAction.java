package com.gluonhq.emoji.action;

import com.gluonhq.emoji.control.EmojiTextArea;
import com.gluonhq.emoji.impl.skin.StyledTextAreaAction;
import com.gluonhq.emoji.impl.skin.EmojiTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Skin;

import java.util.Objects;
import java.util.function.Function;

class BasicAction implements Action {

    private final Function<Action, StyledTextAreaAction> styledTextAreaActionFunction;
    private EmojiTextAreaSkin.EmojiStyledTextArea textArea;

    private final BooleanProperty disabledImplProperty = new SimpleBooleanProperty(this, "disabledImpl", false);


    public BasicAction(EmojiTextArea control, Function<Action, StyledTextAreaAction> styledTextAreaActionFunction) {
        this.styledTextAreaActionFunction = Objects.requireNonNull(styledTextAreaActionFunction);
        if (control.getSkin() != null) {
            initialize(control.getSkin());
        } else {
            control.skinProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (control.getSkin() != null) {
                        initialize(control.getSkin());
                        control.skinProperty().removeListener(this);
                    }
                }
            });
        }
    }

    private void initialize(Skin<?> skin) {
        if (!(skin instanceof EmojiTextAreaSkin)) {
            return;
        }
        textArea = ((EmojiTextAreaSkin) skin).getTextarea();
        BooleanBinding binding = getStyledTextAreaAction().getDisabledBinding(textArea);
        if (binding != null) {
            disabledImplProperty.bind(binding);
        }
    }

    private StyledTextAreaAction getStyledTextAreaAction() {
        return styledTextAreaActionFunction.apply(this);
    }

    @Override
    public void execute(ActionEvent event) {
        Platform.runLater(() -> getStyledTextAreaAction().apply(textArea));
    }

    @Override
    public ReadOnlyBooleanProperty disabledProperty() {
        return disabledImplProperty;
    }
}
