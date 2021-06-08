package ua.focus.javaclass.servisClass;

import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertAndInform {

    public String title, hd, ct;

    public void inform() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Довідка");
        alert.setHeaderText(hd);
        alert.setContentText(ct);
        alert.showAndWait();
    }

    public void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(hd);
        alert.setContentText(ct);
        alert.showAndWait();
    }

    public void confirmation(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(hd);
        alert.setContentText(ct);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            DeleteAllFilesFolder.deleteAllFilesFolder( "ConGoPro/transit/");
            DeleteAllFilesFolder.deleteAllFilesFolder("ConGoPro/out/");
            System.exit(0);
        } else if (option.get() == ButtonType.CANCEL) {
            event.consume();
        }
    }

}
