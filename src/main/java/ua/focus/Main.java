package ua.focus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

import static ua.focus.controller.Controller.openFile;

public class Main extends Application {
    public static boolean dimensionFHD = true;
    public static String icoImage = "/images/Video/focus.png";
    private String sampleFHD = "/view/sampleFHD.fxml";
    private String sampleHD = "/view/sampleHD.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        String sample = sampleFHD;
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (sSize.height < 1080 && sSize.width < 1920) {
            dimensionFHD = false;
            sample = sampleHD;
        }
        Parent root = FXMLLoader.load(getClass().getResource(sample));
        primaryStage.getIcons().add(new Image(getClass().getResource(icoImage).toExternalForm()));
        primaryStage.setTitle("Focus  " + openFile);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
