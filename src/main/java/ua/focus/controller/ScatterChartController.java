package ua.focus.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ua.focus.javaclass.servisClass.OpenStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ua.focus.controller.Controller.openFile;
import static ua.focus.controller.LineChartController.*;

public class ScatterChartController implements Initializable {
    OpenStage os = new OpenStage();
    @FXML
    public ScatterChart scatterChart, scatterChartAlt;
    @FXML
    public Button lineChartButton;

    @Override
    public void initialize( URL location, ResourceBundle resources) {
        NumberAxis x = new NumberAxis();
        x.setAutoRanging(false);
        x.setForceZeroInRange(false);

        NumberAxis y = new NumberAxis();
        y.setAutoRanging(false);
        y.setForceZeroInRange(false);

        ScatterChart<Number, Number> cccGPS = new ScatterChart<Number, Number>(x, y);
        cccGPS.setTitle("Графік GPS " + openFile);
        x.setLabel("Latitude");
        y.setLabel("Longitude");

        XYChart.Series series1 = new XYChart.Series();
        //series1.setName("GPS");
        getGPSData();
        series1.setData(gps);

        scatterChart.getData().addAll(series1);


        XYChart.Series seriesAlt = new XYChart.Series();
        //seriesAlt.setName("Висота");
        getAltData();
        seriesAlt.setData(alt);

        scatterChartAlt.getData().addAll(seriesAlt);
    }

    public void onClickLineChart( ActionEvent actionEvent) throws IOException {
        os.viewURL = "/view/lineChart.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();

        Stage stage = (Stage) lineChartButton.getScene().getWindow();
        stage.close();
    }
}
