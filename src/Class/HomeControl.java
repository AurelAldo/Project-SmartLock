package Class;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Node; // <-- penting: ini Node JavaFX, beda dari org.w3c.dom.Node

import Model.LockModels;
import XML.XMLHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomeControl {

    @FXML private ScrollPane TaskTrack;
    @FXML private ScrollPane LockHalaman;
    @FXML private HBox StatSingkat;

    private VBox taskPreviewBox;
    private VBox lockPreviewBox;

    private static final int MAX_TASK_PREVIEW = 5;
    private static final int MAX_LOCK_PREVIEW = 5;

    private static final String[] HARI = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
    private static final double[] WAKTU_HARIAN = {4.2, 5.5, 3.8, 6.0, 4.9, 7.2, 5.7};

    @FXML
    private void initialize() {
        taskPreviewBox = buildPreviewVBox();
        lockPreviewBox = buildPreviewVBox();

        if (TaskTrack != null) {
            TaskTrack.setFitToWidth(true);
            TaskTrack.setContent(taskPreviewBox);
        }
        if (LockHalaman != null) {
            LockHalaman.setFitToWidth(true);
            LockHalaman.setContent(lockPreviewBox);
        }

        loadTaskPreview();
        loadLockPreview();
        buildSmartphoneUsageChart();
    }

    private void buildSmartphoneUsageChart() {
        if (StatSingkat == null) return;
        StatSingkat.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(HARI));
        xAxis.setStyle("-fx-tick-label-fill: white; -fx-font-size: 9;");

        NumberAxis yAxis = new NumberAxis(0, 8, 2); // 0-8 jam range
        yAxis.setLabel("");
        yAxis.setStyle("-fx-tick-label-fill: white; -fx-font-size: 9;");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setCategoryGap(5);
        barChart.setBarGap(2);
        barChart.setAlternativeColumnFillVisible(false);
        barChart.setAlternativeRowFillVisible(false);
        barChart.setHorizontalGridLinesVisible(true);
        barChart.setVerticalGridLinesVisible(false);
        barChart.setPrefSize(241, 100);
        barChart.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        barChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        barChart.setStyle("-fx-background-color: transparent;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        double totalMinggu = 0;
        for (int i = 0; i < HARI.length; i++) {
            series.getData().add(new XYChart.Data<>(HARI[i], WAKTU_HARIAN[i]));
            totalMinggu += WAKTU_HARIAN[i];
        }
        barChart.getData().add(series);

        int totalJam = (int) totalMinggu;
        int totalMenit = (int) Math.round((totalMinggu - totalJam) * 60);
        Label totalLabel = new Label("Total minggu ini: " + totalJam + "j " + totalMenit + "m");
        totalLabel.setTextFill(Color.WHITE);
        totalLabel.setStyle("-fx-font-size: 10;");

        VBox chartContainer = new VBox(2, barChart, totalLabel);
        chartContainer.setAlignment(Pos.CENTER);

        chartContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            try {
                goToScene("/View/Statistik/statistik.fxml", e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        StatSingkat.getChildren().add(chartContainer);
        HBox.setHgrow(chartContainer, Priority.ALWAYS);

        // Styling bar + background setelah node dirender
        Platform.runLater(() -> {
            // background plot transparan
            Node plotBg = barChart.lookup(".chart-plot-background");
            if (plotBg != null) {
                plotBg.setStyle("-fx-background-color: transparent;");
            }
            // grid line warna putih tipis transparan
            for (Node n : barChart.lookupAll(".chart-horizontal-grid-lines .chart-horizontal-grid-line")) {
                n.setStyle("-fx-stroke: rgba(255,255,255,0.25);");
            }
            // set semua bar hitam
            for (Node n : barChart.lookupAll(".default-color0.chart-bar")) {
                n.setStyle("-fx-bar-fill: black;");
            }
        });
    }

    private void loadTaskPreview() {
        taskPreviewBox.getChildren().clear();

        List<TaskRow> tasks = readTasksFromXML();
        tasks.sort(Comparator.comparingLong(t -> t.sisaHari));

        int shown = 0;
        for (TaskRow t : tasks) {
            if (shown >= MAX_TASK_PREVIEW) break;
            taskPreviewBox.getChildren().add(buildTaskRowNode(t));
            shown++;
        }

        if (tasks.size() > shown) {
            taskPreviewBox.getChildren().add(buildMoreRow("Lihat semua tugas…", e -> {
                try {
                    goToScene("/View/TaskTrack/TaskTrack.fxml", e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }));
        }

        if (shown == 0) {
            taskPreviewBox.getChildren().add(buildEmptyRow("Belum ada tugas."));
        }
    }

    private List<TaskRow> readTasksFromXML() {
        List<TaskRow> list = new ArrayList<>();
        try {
            Document doc = XMLHelper.getDocument();
            if (doc == null) return list;
            NodeList nl = doc.getElementsByTagName("Task");
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                String nama = el.getElementsByTagName("Nama").item(0).getTextContent();
                String deadline = el.getElementsByTagName("Deadline").item(0).getTextContent();
                long sisa = XMLHelper.sisaHari(deadline);
                list.add(new TaskRow(nama, deadline, sisa));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private HBox buildTaskRowNode(TaskRow t) {
        String countdown;
        if (t.sisaHari < 0) countdown = "Lewat " + Math.abs(t.sisaHari) + "d";
        else if (t.sisaHari == 0) countdown = "Hari ini";
        else countdown = t.sisaHari + "d";

        Label nameLbl = new Label(t.nama);
        nameLbl.getStyleClass().add("home-task-name");

        Label cdLbl = new Label(countdown);
        cdLbl.getStyleClass().add("home-task-countdown");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(6, nameLbl, spacer, cdLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4,8,4,8));
        row.getStyleClass().add("home-task-row");

        row.setOnMouseClicked(e -> {
            try {
                goToScene("/View/TaskTrack/TaskTrack.fxml", e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        return row;
    }

    /* --------------------------------------------------
     * LOCK PREVIEW
     * -------------------------------------------------- */
    private void loadLockPreview() {
        lockPreviewBox.getChildren().clear();

        List<LockModels.AppLockSetting> locked = LockModels.LockDataStore.loadLockedApps();
        int shown = 0;
        for (LockModels.AppLockSetting s : locked) {
            if (shown >= MAX_LOCK_PREVIEW) break;
            lockPreviewBox.getChildren().add(buildLockRowNode(s));
            shown++;
        }

        if (locked.size() > shown) {
            lockPreviewBox.getChildren().add(buildMoreRow("Lihat semua lock…", e -> {
                try {
                    goToScene("/View/LockApp/Selector.fxml", e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }));
        }

        if (shown == 0) {
            lockPreviewBox.getChildren().add(buildEmptyRow("Belum ada aplikasi yang dikunci."));
        }
    }

    private HBox buildLockRowNode(LockModels.AppLockSetting s) {
        ImageView iv = new ImageView();
        if (s.getApp().getIcon() != null) {
            iv.setImage(s.getApp().getIcon());
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            iv.setPreserveRatio(true);
        }

        Label nameLbl = new Label(s.getApp().getDisplayName());
        nameLbl.getStyleClass().add("home-lock-name");

        Label limitLbl = new Label(formatDurationHM(s.getDailyLimit()));
        limitLbl.getStyleClass().add("home-lock-limit");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4,8,4,8));
        row.getStyleClass().add("home-lock-row");

        if (iv.getImage() != null) row.getChildren().add(iv);
        row.getChildren().addAll(nameLbl, spacer, limitLbl);

        row.setOnMouseClicked(e -> {
            try {
                goToScene("/View/LockApp/Selector.fxml", e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        return row;
    }

    /* --------------------------------------------------
     * UTIL
     * -------------------------------------------------- */
    private String formatDurationHM(Duration d) {
        if (d == null || d.isZero() || d.isNegative()) return "Tidak dibatasi";
        long h = d.toHours();
        long m = d.minusHours(h).toMinutes();
        return String.format("%02d:%02d/hari", h, m);
    }

    private VBox buildPreviewVBox() {
        VBox v = new VBox(2);
        v.setFillWidth(true);
        v.setPadding(new Insets(4));
        return v;
    }

    private HBox buildMoreRow(String text, javafx.event.EventHandler<javafx.scene.input.MouseEvent> action) {
        Label l = new Label(text);
        l.getStyleClass().add("home-more-label");
        HBox box = new HBox(l);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6,8,6,8));
        box.getStyleClass().add("home-more-row");
        box.setOnMouseClicked(action);
        return box;
    }

    private HBox buildEmptyRow(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("home-empty-label");
        HBox box = new HBox(l);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12,8,12,8));
        box.getStyleClass().add("home-empty-row");
        return box;
    }

    /* --------------------------------------------------
     * NAV BUTTONS
     * -------------------------------------------------- */
    @FXML
    private void tomHome(ActionEvent event) {
        loadTaskPreview();
        loadLockPreview();
    }

    @FXML
    private void tomLock(ActionEvent event) throws IOException {
        goToScene("/View/LockApp/Selector.fxml", event);
    }

    @FXML
    private void tomTask(ActionEvent event) throws IOException {
        goToScene("/View/TaskTrack/TaskTrack.fxml", event);
    }

    @FXML
    private void tomProfile(ActionEvent event) throws IOException {
        goToScene("/View/Profile/profile.fxml", event);
    }

    @FXML
    private void TomStatistik(ActionEvent event) throws IOException {
        goToScene("/View/Statistik/statistik.fxml", event);
    }

    private void goToScene(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
    private void goToScene(String fxml, javafx.scene.input.MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /* --------------------------------------------------
     * TaskRow struct
     * -------------------------------------------------- */
    private static class TaskRow {
        final String nama;
        final String deadline;
        final long sisaHari;
        TaskRow(String n, String d, long s) {
            this.nama = n;
            this.deadline = d;
            this.sisaHari = s;
        }
    }
}
