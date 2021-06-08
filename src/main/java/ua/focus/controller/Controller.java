package ua.focus.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.focus.javaclass.GetSettings;
import ua.focus.javaclass.domain.GPS;
import ua.focus.javaclass.domain.GPSTime;
import ua.focus.javaclass.servisClass.*;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.media.MediaPlayer.Status.PAUSED;
import static javafx.scene.media.MediaPlayer.Status.PLAYING;
import static ua.focus.javaclass.domain.GPSTimeCalculator.gpsTimesBulk;
import static ua.focus.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller implements Initializable {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();

    public static String openFile = "";
    public static String openDirectory;
    public static String localZone;
    public String lineCount;
    public String data;
    public static String urlFocus;
    public String headFile = "Час (мілісек),Локальний час,Широта,Довгота,Висота,Швидкість (м/с)";
    public String headGPS = "Час вимірювання,Час,Широта,Довгота,Висота,Швидкість";
    public Double allTime;
    public Duration duration;
    public Duration currentTime;
    public static Duration timeMark;
    public int colsInpDate = 0;
    public static boolean executeError = false;
    public static List<GPSTime> gpsTimes = new ArrayList<>();
    public static List<GPS> gps = new ArrayList<>();
    public ObservableList<InputDate> inputDatesList = FXCollections.observableArrayList();

    @FXML
    public TextArea outputText;
    public VBox vbox;
    public TableView outputTable;
    public Button playerPlay, playerMute, playerRevers, playerForward, playerRevers1s, playerForward1s, playerRotate;
    public Button tSave, tCalc, tChart, tKml, timeMarkButton;
    public TextField statusBar, labelLineCount;
    public Label statusLabel, timeLabel;
    public Label labelAllTime, labelData;
    public ProgressIndicator progressIndicator;
    public MediaView mediaView;
    public Slider timeSlider, volumeSlider;
    public MediaPlayer mediaPlayer;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getSettings.getSettings();
    }

    @SneakyThrows
    public void onClickOpenFile() {
        if (openFile.isEmpty()) {
            onKeyPressed();
            progressIndicatorRun();
            fileChooserRun.openFileChooser();
            openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
            openDirectory = selectedOpenFile.getParent();
            mediaPlayerRun();
            tCalc.setDisable(false);
            progressIndicator.setVisible(false);
            playerPlay.setDisable(false);
            playerPlay.setGraphic(new ImageView("/images/player/pause.png"));
            requestFocus(playerPlay);
        } else {
            statusBar.setText("Файл уже відкрито!");
            inform.hd = " Файл уже відкрито! \n При відкритті нового файлу розраховані дані буде втрачено";
            inform.ct = " 1. Збережіть розраховані дані \n 2. Натисніть кнопку \"Новий бланк\" \n 3. Відкрийте файл \n";
            inform.inform();
        }
    }

    public void openData() throws Exception {

        File conGoProOut = new File("ConGoPro/out/" + openFile + "-gps.csv");
        try {
            FileReader fileReader = new FileReader(conGoProOut);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int lineNumber = 0;
            String line;
            String timeStart = "";
            String timeStop = "";
            String dataStart = "";

            while ((line = bufferedReader.readLine()) != null) {

                if (lineNumber == 1) {
                    timeStart = line.split(",")[0];
                    dataStart = line.split(",")[6];
                }

                line = line.replaceAll(";", ",");

                String[] split = line.split(",");
                if (lineNumber < 1) {
                    lineNumber++;
                    continue;
                }
                lineNumber++;

                timeStop = String.valueOf(line.split(",")[0]);

                GPS gpses = new GPS(
                        split[0],
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3]),
                        Double.parseDouble(split[4]),
                        Double.parseDouble(split[5]),
                        Long.parseLong(split[6]));
                gps.add(gpses);
            }

            gpsTimes = gpsTimesBulk(gps);

            fileReader.close();

            lineCount = String.valueOf(lineNumber);
            labelLineCount.setText("Cтрок:  " + lineCount);

            allTime = (Double.parseDouble(timeStop) - Double.parseDouble(timeStart)) / 1000;

            // DATA
            long unixSeconds = Long.parseLong(dataStart);
            Date date = new Date(unixSeconds / 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            data = sdf.format(date);

            statusLabel.setText(headGPS);
            statusBar.setText(headGPS);
            labelAllTime.setText(" Час вимірювання " + allTime + " сек");
            labelData.setText(" Дата  " + data);
        } catch (Exception e) {
            inform.hd = "Помилка!";
            inform.hd = "Не вдалося вилучити дані";
            inform.ct = "  Відеофайл не містить інформації про просторове положення або пошкоджений.\n \n  Назва файлу повинна складатися з латинських літер або цифр та не містити пробілів.\n \n  Шлях до каталогу розміщення відеофайлів повинен складатися з латинських літер або цифр та не містити пробілів.";
            inform.alert();
            executeError = true;
        }
    }

    @SneakyThrows
    public void onClickCalculate() {
        playerPause();
        ffmpeg();
        statusBar.setText("Вилучення даних...");
        Process process = Runtime.getRuntime().exec("cmd /c start ffmpeg.bat");
        String s = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
//        outputText.setText(s + "\n");
//        outputText.setText(error + "\n");
        final int exitVal = process.waitFor();
        if (exitVal == 0) {
            openData();
        }
        //output to Table----------------------------------------
        if (executeError == false) {
            inputDates(gpsTimes);
            TableColumn<InputDate, String> tTime = new TableColumn<>("Час, мс");
            TableColumn<InputDate, String> tLocalTime = new TableColumn<>("Локальний час");
            TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
            TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
            TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота, м");
            TableColumn<InputDate, String> tSpeed = new TableColumn<>("Швидкість, м/с");

            for (int i = 0; i <= colsInpDate - 6; i++) {
                final int indexColumn = i;
                tTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                tLocalTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                tSpeed.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
            }
            outputTable.getColumns().addAll(tTime, tLocalTime, tLat, tLong, tAlt, tSpeed);
            outputTable.setItems(inputDatesList);
            //--------------------------------------------------------
            statusBar.setText(openFile);
            statusLabel.setText("Дані  GPS");
            playerPlay.setDisable(false);
            tChart.setDisable(false);
            tKml.setDisable(false);
            tSave.setDisable(false);
            requestFocus(playerPlay);
        }
    }

    @SneakyThrows
    public void onClickSave() {
        progressIndicatorRun();
        if (CollectionUtils.isEmpty(gpsTimes)) {
            log.warn("gpsTime is empty");
            statusBar.setText("Помилка! Відсутні дані для збереження");
            inform.hd = "Помилка! Відсутні дані для збереження";
            inform.ct = " 1. Відкрити підготовлений файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            progressIndicator.setVisible(false);
            statusBar.setText("");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"),
                new FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new FileChooser.ExtensionFilter("Всі файли", "*.*"));
        fileChooser.setInitialFileName(openFile + "_gps");
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog((new Stage()));
//-------------------------------
        if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
            Workbook book = new XSSFWorkbook();
            Sheet sheet = book.createSheet(openFile + "_gps");

            DataFormat format = book.createDataFormat();
            CellStyle dateStyle = book.createCellStyle();
            dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

            int rownum = 0;
            Cell cell;
            Row row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue("Дата");
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellStyle(dateStyle);
            cell.setCellValue(data);
            rownum++;
            row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue("Час вимірювання");
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(allTime);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue("секунд");
            rownum++;
            row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue("Часовий  пояс");
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(localZone);
            rownum++;
            row = sheet.createRow(rownum);

            for (int colnum = 0; colnum <= StringUtils.countMatches(headFile, ","); colnum++) {
                cell = row.createCell(colnum, CellType.STRING);
                cell.setCellValue(headFile.split(",")[colnum]);
                sheet.autoSizeColumn(colnum);
            }
            rownum++;

            for (GPSTime gpsTimes : gpsTimes) {
                row = sheet.createRow(rownum);
                for (int colnum = 0; colnum <= StringUtils.countMatches(headFile, ","); colnum++) {
                    cell = row.createCell(colnum, CellType.NUMERIC);
                    cell.setCellValue(gpsTimes.toString().split(",")[colnum]);
                }
                rownum++;
            }

            FileOutputStream outFile = new FileOutputStream(file);
            book.write(outFile);
            outFile.close();
        }
//------------------------
        if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), "Cp1251");
            osw.write("Дата:,    " + data + "\n");
            osw.write("Час вимірювання:,    " + allTime + "\n");
            osw.write("Часовий  пояс:,   " + localZone + "  \n\n");
            osw.write(headFile + "\n");
            for (GPSTime gpsTimes : gpsTimes) {
                osw.write(gpsTimes.toString());
            }
            osw.close();
        }
//_______________________
        statusBar.setText("Успішно записано в файл '" + openFile + "_gps'");
        inform.title = "Збереження файлу";
        inform.hd = null;
        inform.ct = "Успішно записано в файл '" + openFile + "_gps'";
        inform.inform();
        progressIndicator.setVisible(false);
    }

    @SneakyThrows
    public void onClickKML() {
        if (outputTable.getItems() == null) {
            statusBar.setText("Помилка! Відсутні дані для рохрахунку");
            inform.hd = "Помилка! Відсутні дані для рохрахунку";
            inform.ct = " 1. Відкрити файл  даних \n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            statusBar.setText("");
        } else {
            //output to Table----------------------------------------
            inputDates(gpsTimes);
            TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
            TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
            TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");

            for (int i = 0; i <= colsInpDate - 6; i++) {
                final int indexColumn = i;
                tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
            }
            outputTable.getColumns().addAll(tLong, tLat, tAlt);
            outputTable.setItems(inputDatesList);
            //--------------------------------------------------------
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Зберегти як...");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(".kml", "*.kml"));
            fileChooser.setInitialFileName(openFile + "_kml");
            File userDirectory = new File(openDirectory);
            fileChooser.setInitialDirectory(userDirectory);

            File file = fileChooser.showSaveDialog((new Stage()));

            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");
            osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            osw.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
            osw.write("<Document>\n");
            osw.write("<name>" + openFile + "</name>\n");
            osw.write("<visiblity>1</visiblity>\n");
            osw.write("<description>Exported track data\n" + openFile + "</description>\n");
            osw.write("\n");
            osw.write("<Style id=\"trackcolor_" + openFile + "_1\">\n");
            osw.write("<LineStyle>\n");
            osw.write("<color>ff0ff0ff</color>\n");
            osw.write("<width>4</width>\n");
            osw.write("</LineStyle>\n");
            osw.write("</Style>\n\n");
            osw.write("<Placemark>\n");
            osw.write("<name>" + openFile + "</name>\n");
            osw.write("<visibility>1</visibility>\n");
            osw.write("<description>Exported track data</description>\n");
            osw.write("<styleUrl>#trackcolor_" + openFile + "_1\">\n</styleUrl>\n");
            osw.write("<LineString>\n");
            osw.write("<tessellate>1</tessellate>\n");
            osw.write("<altitudeMode>absolute</altitudeMode>\n");
            osw.write("<coordinates>\n");
            for (GPSTime gpsTimes : gpsTimes) {
                osw.write(gpsTimes.toStringKML());
            }
            osw.write("</coordinates>\n");
            osw.write("</LineString>\n");
            osw.write("</Placemark>\n");
            osw.write("</Document>\n");
            osw.write("</kml>\n");
            osw.close();

            statusBar.setText("Успішно записано в файл '" + openFile + "_kml'");
            statusLabel.setText("KLM");
            inform.title = "Збереження файлу";
            inform.hd = null;
            inform.ct = "Успішно записано в файл '" + openFile + "_kml'";
            inform.inform();
        }
    }

    public void onClickChart() throws IOException {
        progressIndicator.setVisible(true);
        if (statusLabel.getText().equals("Дані  GPS") || statusLabel.getText().equals("KLM")) {
            os.viewURL = "/view/lineChart.fxml";
            os.title = "Графік GPS   " + openFile;
            os.openStage();
        } else {
            statusBar.setText("Помилка! Відсутні дані для рохрахунку");
            inform.hd = "Помилка! Відсутні дані для відображення";
            inform.ct = " Необхідно відкрити підготовлений файл вхідних даних\n ";
            inform.alert();
            statusBar.setText("");
        }
        progressIndicator.setVisible(false);
    }

    @SneakyThrows
    public void onClickSetTimeMark() {
        playerPause();
        timeMark = mediaPlayer.getCurrentTime();
        os.viewURL = "/view/setTimeMark.fxml";
        os.title = "Створення мітки часу   " + openFile;
        os.maximized = false;
        os.isResizable = false;
        os.openStage();
    }

    @SneakyThrows
    public void onClickGetTimeMark() {
        os.viewURL = "/view/timeMarkView.fxml";
        os.title = "Перегляд міток часу   " + openFile;
        os.maximized = false;
        os.isResizable = false;
        os.isModality = false;
        os.openStage();
    }

    @SneakyThrows
    public void ffmpeg() {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("ffmpeg.bat", false), "UTF-8");
        osw.write("%CD%/ConGoPro/Programs/ffmpeg/bin/ffmpeg.exe -y -i   " + openDirectory + "/" + openFile + ".MP4 -codec copy -map 0:3 -f rawvideo " + "%CD%/ConGoPro/transit/" + openFile + ".bin \n");
        //osw.write("pause \n");
        osw.write("%CD%/ConGoPro/Programs/gpmd2csv/gpmd2csv.exe  -i  " + "%CD%/ConGoPro/transit/" + openFile + ".bin  " + "-o " + "%CD%/ConGoPro/out/" + openFile + ".csv \n");
        //osw.write("pause \n");
        osw.write("exit");
        osw.close();
    }

    public void mediaPlayerRun() {
        playerMute.setDisable(false);
        playerRevers.setDisable(false);
        playerForward.setDisable(false);
        playerRevers1s.setDisable(false);
        playerForward1s.setDisable(false);
        playerRotate.setDisable(false);
        String url = Paths.get(String.valueOf(selectedOpenFile)).toUri().toString();

        if (selectedOpenFile != null) {
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            if (mediaPlayer.getStatus() == PLAYING) {
                mediaPlayer.stop();
            }
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnReady(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable ov) {
                            currentTime = mediaPlayer.getCurrentTime();
                            duration = mediaPlayer.getTotalDuration();
                            if (!timeSlider.isValueChanging() && duration.greaterThan(Duration.ZERO)) {
                                timeSlider.setValue(currentTime.toMillis() / duration.toMillis() * 100);
                            }
                            timeLabel.setText(currentTime.toString().replaceAll("ms", "") + " / " + duration);
                        }
                    });
                    timeSlider.valueChangingProperty().addListener(new InvalidationListener() {
                        public void invalidated(Observable ov) {
                            mediaPlayer.seek(mediaPlayer.getTotalDuration()
                                    .multiply(timeSlider.getValue() / 100.0));
                        }
                    });
                    volumeSlider.valueProperty().set(70);
                    volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if (volumeSlider.isValueChanging()) {
                                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                            }
                        }
                    });
                    //-----------------------------------------------
                    String audio = mediaPlayer.getMedia().getTracks().toString()
                            .replace("[", "")
                            .replace("]", "")
                            .split("javafx.scene.media.")[1];
                    String video = mediaPlayer.getMedia().getTracks().toString()
                            .replace("[", "")
                            .replace("]", "")
                            .split("javafx.scene.media.")[2];

                    outputText.setText(String.valueOf(new StringBuilder()
                            .append("Інформація про відео\n")
                            .append(video)
                            .append("\n")
                            .append("Інформація про звук\n")
                            .append(audio)
                            .append("\n")
                            .append("Розміщення файлу \n")
                            .append(mediaPlayer.getMedia().getSource())
                    ));
                    statusBar.setText("Відтворюється відео  " + openFile + ".MP4");
                }
            });
        }
        timeMarkButton.setDisable(false);
    }

    public void inputDates(List source) {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        outputTable.setEditable(false);
        int rowsInpDate = 0;
        String line;
        String splitBy = ",";
        for (int j = 0; j < source.size(); j++) {
            line = source.get(j).toString();
            line = line.replace("[", "").replace("]", "");
            rowsInpDate += 1;
            String[] fields = line.split(splitBy, -1);
            colsInpDate = fields.length;
            InputDate inpd = new InputDate(fields);
            inputDatesList.add(inpd);
        }
    }

    public void onClickNew() {
        playerClose();
        openFile = "";
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        outputText.setText("");
        statusBar.setText("");
        statusLabel.setText("");
        labelLineCount.setText("");
        labelAllTime.setText("");
        timeLabel.setText("");
        gps.clear();
        gpsTimes.clear();
        tChart.setDisable(true);
        tKml.setDisable(true);
        tSave.setDisable(true);
        tCalc.setDisable(true);
        timeMarkButton.setDisable(true);
        progressIndicator.setVisible(false);
        DeleteAllFilesFolder.deleteAllFilesFolder("%CD%/ConGoPro/transit/");
        DeleteAllFilesFolder.deleteAllFilesFolder("%CD%/ConGoPro/out/");
        executeError = false;
    }

    @SneakyThrows
    public void onClickGoogleEarth() {
        Process process = Runtime.getRuntime().exec("cmd.exe /c start " + "googleearth/GoogleEarthPro.exe ");

    }

    public void onClickOpenFileInDesktop() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
    }

    @SneakyThrows
    public void onClickHelp() {
        String url = "userManual/UserManual.pdf";
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(url));
    }

    @SneakyThrows
    public void onClickMenuGoPro() {
        if (Desktop.isDesktopSupported()) {
            String url = "userManual/HERO7UM_RU_REVA.pdf";
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(url));
        }
    }

    @SneakyThrows
    public void onClickMenuHotKeysList() {
        if (Desktop.isDesktopSupported()) {
            String url = "userManual/Hot_Keys.pdf";
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(url));
        }
    }

    public void onClick_menuAbout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickSettings() throws IOException {
        os.viewURL = "/view/settings.fxml";
        os.title = "Налаштування   " + openFile;
        os.maximized = false;
        os.isResizable = false;
        os.isModality = true;
        os.openStage();
    }

    public void progressIndicatorRun() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
            statusBar.setText("Зачекайте...");
        });
    }

    public void onClickPlayerPlay() {
        if (mediaPlayer.getStatus() == PLAYING) {
            playerPlay.setGraphic(new ImageView("/images/player/play.png"));
            mediaPlayer.pause();
        } else {
            if (mediaPlayer.getStatus() == PAUSED) {
                playerPlay.setGraphic(new ImageView("/images/player/pause.png"));
                mediaPlayer.play();
            }
        }
        playerPlay.requestFocus();
    }

    public void playerPause() {
        mediaPlayer.pause();
        playerPlay.setGraphic(new ImageView("/images/player/play.png"));
        requestFocus(playerPlay);
    }

    public void playerMute() {
        if (mediaPlayer.isMute() == false) {
            mediaPlayer.setMute(true);
            playerMute.setGraphic(new ImageView("/images/player/mute_off.png"));
        } else {
            mediaPlayer.setMute(false);
            playerMute.setGraphic(new ImageView("/images/player/mute_on.png"));
        }
    }

    public void onClickForward() {
        moveForward(10.0);
    }

    public void onClickForward1s() {
        moveForward(1000.0);
    }

    public void onClickRevers() {
        moveRevers(10.0);
    }

    public void onClickRevers1s() {
        moveRevers(1000.0);
    }

    public void moveForward(Double moveMilliseconds) {
        if (currentTime.toMillis() < (duration.toMillis() - moveMilliseconds + 10.1)) {
            mediaPlayer.seek(Duration.millis(currentTime.toMillis() + moveMilliseconds));
            timeSlider.setValue(currentTime.toMillis() + moveMilliseconds);
        }
        playerPause();
    }

    public void moveRevers(Double moveMilliseconds) {
        if (currentTime.toMillis() > moveMilliseconds + 10.0) {
            mediaPlayer.seek(Duration.millis(currentTime.toMillis() - moveMilliseconds));
            timeSlider.setValue(currentTime.toMillis() - moveMilliseconds);
        }
        playerPause();
    }

    public void onKeyPressed() {
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case SPACE: {
                    onClickPlayerPlay();
                    break;
                }
                case M: {
                    playerMute();
                    break;
                }
                case F1: {
                    onClickHelp();
                    break;
                }
                case PAGE_DOWN: {
                    moveForward(1000.0);
                    break;
                }
                case PAGE_UP: {
                    moveRevers(1000.0);
                    break;
                }
                case PERIOD: {
                    moveForward(10.0);
                    break;
                }
                case COMMA: {
                    moveRevers(10.0);
                    break;
                }
                case C: {
                    if (event.isAltDown())
                        onClickCalculate();
                    break;
                }
                case T: {
                    if (event.isAltDown())
                        onClickSetTimeMark();
                    break;
                }
                case K: {
                    if (event.isAltDown())
                        onClickKML();
                    break;
                }
                case G: {
                    if (event.isAltDown())
                        onClickGoogleEarth();
                    break;
                }
                case S: {
                    if (event.isControlDown())
                        onClickSave();
                    break;
                }
                case N: {
                    if (event.isControlDown())
                        onClickNew();
                    break;
                }
                case Q: {
                    if (event.isControlDown())
                        onClickRotate();
                    break;
                }
            }
            if (event.getText().equals("ю")) {
                moveForward(10.0);
            }
            if (event.getText().equals("б")) {
                moveRevers(10.0);
            }
        });
    }

    public void onClickRotate() {
        switch ((int) mediaView.getFitHeight()) {
            case 690: mediaView.setFitHeight(420);
                break;
            case 420: mediaView.setFitHeight(690);
                break;
            case 450: mediaView.setFitHeight(270);
                break;
            case 270: mediaView.setFitHeight(450);
                break;
        }

            mediaView.setRotate(mediaView.getRotate() + 90);

    }

    public void requestFocus(Node node) {
        node.requestFocus();
    }

    public void playerClose() {
        mediaView.setMediaPlayer(null);
        mediaPlayer.dispose();
        timeSlider.adjustValue(0);
        timeLabel.setText("");
        playerMute.setDisable(true);
        playerPlay.setDisable(true);
        playerRevers.setDisable(true);
        playerForward.setDisable(true);
        playerRevers1s.setDisable(true);
        playerForward1s.setDisable(true);
        playerRotate.setDisable(true);
    }

    public void onClickCancelBtn(ActionEvent event) {
        inform.title = "Вихід з програми";
        inform.hd = "Закрити програму?";
        inform.ct = "Всі незбережені дані буде втрачено";
        inform.confirmation(event);
    }
}




