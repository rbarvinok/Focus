package ua.focus.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ua.focus.javaclass.domain.GPSTime;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.focus.controller.Controller.openFile;

public class ChartVelocityController implements Initializable {

    @FXML
    public LineChart lineChartTime, lineChartAlt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        LineChart<Number, Number> lcc = new LineChart<Number, Number>(x, y);
        lcc.setTitle("Швидкість" + openFile);
        x.setLabel("Час,c");
        y.setLabel("Швидкість");

        XYChart.Series series = new XYChart.Series();
        series.setName("Швидкість");


        List<GPSTime.Speed> TimeVelocity = Controller.gpsTimes.stream().map(GPSTime -> {
            return new GPSTime.Speed(GPSTime.getTime(), GPSTime.getSpeed());
        }).collect(Collectors.toList());

        ObservableList<XYChart.Data> Vel = FXCollections.observableArrayList();
        for (GPSTime.Speed timeVelocity : TimeVelocity) {
            Vel.add(new XYChart.Data(Double.parseDouble(timeVelocity.getTime()), timeVelocity.getSpeed()));
        }

        series.setData(Vel);

        lineChartTime.getData().addAll(series);



    }
}