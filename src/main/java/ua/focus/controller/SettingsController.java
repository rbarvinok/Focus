package ua.focus.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.focus.javaclass.servisClass.AlertAndInform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ResourceBundle;

import static ua.focus.controller.Controller.localZone;

public class SettingsController implements Initializable {
    AlertAndInform inform = new AlertAndInform();

    @FXML
    public TextField GMTInput;
    public Label timeLabel;
    public Button SaveNewGMT, changeGMT, utc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GMTInput.setText(String.valueOf(localZone));
        if (localZone.equals("GMT+03:00")) {
            timeLabel.setText("Літній час");
            changeGMT.setText("Встановити зимовий час");
        } else {
            if (localZone.equals("GMT+02:00")) {
                timeLabel.setText("Зимовий час");
                changeGMT.setText("Встановити літній час");
            } else
                changeGMT.setText("Встановити зимовий час");
        }
    }

    public void onClickNewSettings() throws IOException {
        if (GMTInput.getText().equals("")) {
            inform.hd = "Помилка!";
            inform.hd = "Невірний формат даних\n";
            inform.ct = "Поле для вводу не може бути пустим та має містити тільки цифрові значення \n";
            inform.alert();
            GMTInput.setText("");
            return;
        }
        localZone = GMTInput.getText().replace(",", ".");

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("settings.txt", false), "UTF-8");
        osw.write("GMT=" + localZone + "\n");
        osw.close();

        Stage stage = (Stage) SaveNewGMT.getScene().getWindow();
        stage.close();
    }

    public void onClickChangeGMT() {
        if (changeGMT.getText() == "Встановити зимовий час") {
            localZone = "GMT+02:00";
            GMTInput.setText(localZone);
            timeLabel.setText("Зимовий час");
            changeGMT.setText("Встановити літній час");
        } else if (changeGMT.getText() == "Встановити літній час") {
            localZone = "GMT+03:00";
            GMTInput.setText(localZone);
            timeLabel.setText("Літній час");
            changeGMT.setText("Встановити зимовий час");
        }
    }

    public void onClickChangeUTC() {
        localZone = "GMT";
        GMTInput.setText(localZone);
        timeLabel.setText("Час UTC");
    }

    public void onClickEditURL() {
        GMTInput.setDisable(false);
        GMTInput.setEditable(true);
        GMTInput.requestFocus();
    }

}
