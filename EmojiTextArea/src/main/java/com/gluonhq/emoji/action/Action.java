package com.gluonhq.emoji.action;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;

public interface Action {

    void execute(ActionEvent event);

    ReadOnlyBooleanProperty disabledProperty();

}