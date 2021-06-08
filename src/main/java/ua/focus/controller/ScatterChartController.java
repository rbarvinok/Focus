package ua.focus.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ua.focus.javaclass.servisClass.OpenStage;

import java.net.URL;
import java.util.ResourceBundle;

import static ua.focus.controller.Controller.openFile;
import static ua.focus.controller.LineChartController.*;

public class ScatterChartController implements Initializable {
    OpenStage os = new OpenStage();
    @FXML
    public ScatterChart scatterChart, scatterChartAlt;
    public Button lineChartButton, velocityChartButton;
    public TextArea outputText;

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
        getGPSData();
        series1.setData(gps);

        scatterChart.getData().addAll(series1);

        XYChart.Series seriesAlt = new XYChart.Series();
        getAltData();
        seriesAlt.setData(alt);

        scatterChartAlt.getData().addAll(seriesAlt);

        consolidatedData(outputText);
        getCurrentData();
        getCurrentDataAlt();
    }

    @SneakyThrows
    public void onClickLineChart( ActionEvent actionEvent) {
        os.viewURL = "/view/lineChart.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();

        Stage stage = (Stage) lineChartButton.getScene().getWindow();
        stage.close();
    }

    @SneakyThrows
    public void onClickVelocityChart(ActionEvent actionEvent) {
        os.viewURL = "/view/chartVelocity.fxml";
        os.title = "Графік швидкості - " + openFile;
        os.maximized = false;
        os.openStage();
        Stage stage = (Stage) velocityChartButton.getScene().getWindow();
        stage.close();
    }

    public void getCurrentData() {
        ObservableList<XYChart.Data> dataList = ((XYChart.Series) scatterChart.getData().get(0)).getData();
        for (XYChart.Data data : dataList) {
            Node node = data.getNode();
            Tooltip tooltip = new Tooltip("Широта: " + data.getXValue().toString() + '\n' + "Довгота: " + data.getYValue().toString());
            Tooltip.install(node, tooltip);

            node.setOnMouseEntered(event -> node.getStyleClass().add("onHover"));
            node.setOnMouseExited(event -> node.getStyleClass().remove("onHover"));
        }
    }

    public void getCurrentDataAlt() {
        ObservableList<XYChart.Data> dataList = ((XYChart.Series) scatterChartAlt.getData().get(0)).getData();
        for (XYChart.Data data : dataList) {
            Node node = data.getNode();
            Tooltip tooltip = new Tooltip("Час: " + data.getXValue().toString() + '\n' + "Висота: " + data.getYValue().toString());
            Tooltip.install(node, tooltip);

            node.setOnMouseEntered(event -> node.getStyleClass().add("onHover"));
            node.setOnMouseExited(event -> node.getStyleClass().remove("onHover"));
        }
    }
}
