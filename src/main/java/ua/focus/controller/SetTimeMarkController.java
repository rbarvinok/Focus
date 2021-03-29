package ua.focus.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.focus.javaclass.servisClass.AlertAndInform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ResourceBundle;

import static ua.focus.controller.Controller.openFile;
import static ua.focus.controller.Controller.timeMark;

public class SetTimeMarkController implements Initializable {
    public String comment;
    AlertAndInform inform = new AlertAndInform();

    @FXML
    public TextField commentField, timeLabel;

    @FXML
    public Button saveBtn;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {
        timeLabel.setText(String.valueOf(timeMark));
    }

    public void onClickNewTimeMark( ActionEvent event ) throws IOException {

        if (commentField.getText().equals("")) {
            inform.hd = "Помилка!";
            inform.hd = "Невірний формат даних\n";
            inform.ct = "Поле для вводу не може бути пустим \n";
            inform.alert();
            commentField.setText("");
            return;
        }
        comment = commentField.getText();

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("timeMark.txt", true), "Cp1251");
        osw.write("Файл:  " + openFile + "\n");
        osw.write("Час:  " + timeMark + "\n");
        osw.write(comment + "\n \n");
        osw.close();

        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    public void onClickReset( ActionEvent actionEvent ) {
        commentField.setText("");
    }
}
