module com.gluonhq.emoji {
    requires javafx.controls;
    requires com.google.gson;
    opens com.gluonhq.emoji to com.google.gson;

    exports com.gluonhq.emoji;
    exports com.gluonhq.emoji.util;
}