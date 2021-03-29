package ua.focus.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;
import ua.focus.javaclass.servisClass.OpenStage;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class TimeMarkController implements Initializable {
    OpenStage os = new OpenStage();
    @FXML
    public TextArea outputText;
    @FXML
    public Button reset;

    @SneakyThrows
    @Override
    public void initialize( URL location, ResourceBundle resources ) {
        FileReader fileReader = new FileReader("timeMark.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        //int lineNumber = 0;
        String line = "";
        String textFieldReadable = bufferedReader.readLine();
        while (textFieldReadable != null) {
            line += textFieldReadable + "\n";
            textFieldReadable = bufferedReader.readLine();
            outputText.setText(line);
        }
        fileReader.close();
    }

    @SneakyThrows
    public void onClickDelete( ActionEvent actionEvent ) {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("timeMark.txt", false), StandardCharsets.UTF_8);
        osw.write("");
        osw.close();

        os.closeButton=reset;
        os.closeStage();
        outputText.setText("");
    }
}
