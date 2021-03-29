package ua.focus.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ua.focus.javaclass.domain.GPSTime;
import ua.focus.javaclass.servisClass.OpenStage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.focus.controller.Controller.openFile;

public class LineChartController implements Initializable {
    OpenStage os = new OpenStage();
    public static ObservableList<XYChart.Data> gps;
    public static ObservableList<XYChart.Data> alt;

    @FXML
    public LineChart lineChart, lineChartAlt;
    public Button scatterChartButton, velocityChartButton;


    @Override
    public void initialize( URL location, ResourceBundle resources ) {

        NumberAxis x = new NumberAxis();
        x.setAutoRanging(false);
        x.setForceZeroInRange(false);

        NumberAxis y = new NumberAxis();
        y.setAutoRanging(false);
        y.setForceZeroInRange(false);

        LineChart<Number, Number> lcc = new LineChart<Number, Number>(x, y);
        lcc.setTitle("Графік GPS " + openFile);
        x.setLabel("Latitude");
        y.setLabel("Longitude");

        XYChart.Series series1 = new XYChart.Series();
        //series1.setName("GPS");
        getGPSData();
        series1.setData(gps);

        lineChart.getData().addAll(series1);


        XYChart.Series seriesAlt = new XYChart.Series();
        //series1.setName("Висота");
        getAltData();
        seriesAlt.setData(alt);

        lineChartAlt.getData().addAll(seriesAlt);
    }

    public static void getGPSData() {
        List<GPSTime.Latitude> gpsLatitude = Controller.gpsTimes.stream().map(gpsTimes -> {
            return new GPSTime.Latitude(gpsTimes.getLongitude(), gpsTimes.getLatitude());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (GPSTime.Latitude latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLongitude(), latitude.getLatitude()));
        }
    }

    //        Alt --------------------------------
    public static void getAltData() {
        List<GPSTime.Altitude> gpsAlt = Controller.gpsTimes.stream().map(gpsTimes -> {
            return new GPSTime.Altitude(gpsTimes.getTime(), gpsTimes.getAltitude());
        }).collect(Collectors.toList());

        alt = FXCollections.observableArrayList();
        for (GPSTime.Altitude altitude : gpsAlt) {
            alt.add(new XYChart.Data(Double.parseDouble(altitude.getTime()), altitude.getAltitude()));
        }
    }

    @SneakyThrows
    public void onClickScatterChart( ActionEvent actionEvent ) {
        os.viewURL = "/view/scatterChart.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();
        Stage stage = (Stage) scatterChartButton.getScene().getWindow();
        stage.close();
    }

    @SneakyThrows
    public void onClickVelocityChart( ActionEvent actionEvent ) {
        os.viewURL = "/view/chartVelocity.fxml";
        os.title = "Графік швидкості - " + openFile;
        os.maximized = false;
        os.openStage();
        Stage stage = (Stage) velocityChartButton.getScene().getWindow();
        stage.close();
    }
}