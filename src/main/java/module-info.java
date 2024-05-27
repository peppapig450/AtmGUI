module com.nick.atmInterface {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires java.sql;

    opens com.nick.atmInterface to javafx.fxml;

    exports com.nick.atmInterface;
}
