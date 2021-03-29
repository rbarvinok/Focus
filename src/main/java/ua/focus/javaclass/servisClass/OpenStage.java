package ua.focus.javaclass.servisClass;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static ua.focus.Main.icoImage;

public class OpenStage {
    public String viewURL;
    public String imageURL = icoImage;
    public String title;
    public boolean maximized = false;
    public boolean isModality = false;
    public boolean isResizable = true;
    public Button closeButton;

    public void openStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewURL));
        Parent root = fxmlLoader.load();
        stage.setTitle(title);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(imageURL)));
        stage.setScene(new Scene(root));
        stage.setMaximized(maximized);
        stage.setResizable(isResizable);
        if (isModality = true)
            stage.initModality(Modality.APPLICATION_MODAL);
        else
            stage.initModality(Modality.NONE);
        stage.show();
    }

    public void closeStage() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
