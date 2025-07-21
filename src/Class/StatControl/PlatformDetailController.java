package Class.StatControl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Side;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlatformDetailController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private Button btnFilter;
    @FXML private Label headerLabel;
    @FXML private Label averageLabel;
    @FXML private ImageView platformIcon;

    private static final Map<String, PlatformData> PLATFORM_DATA = new HashMap<>();

        static {
        PLATFORM_DATA.put("Instagram", new PlatformData(
                "Instagram", ": 2 jam 37 menit", "instagram (1).png",
                new String[]{"Sen","Sel","Rab","Kam","Jum","Sab","Min"},
                new double[]{1.9, 2.0, 2.5, 3.1, 3.0, 3.4, 2.4}, "#5865F2"));

        PLATFORM_DATA.put("TikTok", new PlatformData(
                "TikTok", ": 3 jam 38 menit", "tiktok.png",
                new String[]{"Sen","Sel","Rab","Kam","Jum","Sab","Min"},
                new double[]{3.6, 3.2, 4.0, 3.5, 4.3, 3.9, 3.0}, "#000000"));

        PLATFORM_DATA.put("YouTube", new PlatformData(  // Ditukar posisinya
                "YouTube", ": 1 jam 13 menit", "youtube.png",
                new String[]{"Sen","Sel","Rab","Kam","Jum","Sab","Min"},
                new double[]{0.4, 1.5, 1.2, 0.8, 1.6, 1.5, 1.5}, "#1DA1F2"));

        PLATFORM_DATA.put("MLBB", new PlatformData(     // Ditukar posisinya
                "MLBB", ": 1 jam 56 menit", "mlbb.jpg",
                new String[]{"Sen","Sel","Rab","Kam","Jum","Sab","Min"},
                new double[]{2.1, 1.8, 2.5, 2.3, 1.9, 1.6, 1.3}, "#FF0000"));
    }



    private String currentPlatform = "Instagram";
    private boolean showingBarChart = true;

    @FXML
    public void initialize() {
        setupFilterDropdown();
        setPlatform(currentPlatform);
    }

    public void setPlatform(String platform) {
        this.currentPlatform = platform;
        PlatformData data = PLATFORM_DATA.get(platform);
        if (data == null) return;

        headerLabel.setText(data.name);
        averageLabel.setText(data.averageTime);

        try {
            Image img = new Image(getClass().getResourceAsStream("/AsetSB/" + data.iconFileName));
            platformIcon.setImage(img);
        } catch (Exception e) {
            System.out.println("Gagal memuat ikon: " + data.iconFileName);
        }

        updateCharts(data);
    }

    private void setupFilterDropdown() {
        ContextMenu menu = new ContextMenu();
        MenuItem bar = new MenuItem("Bar Chart");
        bar.setOnAction(e -> {
            showingBarChart = true;
            setPlatform(currentPlatform);
            btnFilter.setText("Bar Chart");
        });
        MenuItem pie = new MenuItem("Pie Chart");
        pie.setOnAction(e -> {
            showingBarChart = false;
            setPlatform(currentPlatform);
            btnFilter.setText("Pie Chart");
        });
        menu.getItems().addAll(bar, pie);
        btnFilter.setUserData(menu);
    }

    private void updateCharts(PlatformData data) {
        barChart.setVisible(showingBarChart);
        pieChart.setVisible(!showingBarChart);

        if (showingBarChart) {
            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Waktu (jam)");
            for (int i = 0; i < data.days.length; i++) {
                series.getData().add(new XYChart.Data<>(data.days[i], data.weeklyData[i]));
            }
            barChart.getData().add(series);

            Platform.runLater(() -> {
                var bg = barChart.lookup(".chart-plot-background");
                if (bg != null) bg.setStyle("-fx-background-color: #f8f8f8;");
            });

        } else {
            pieChart.getData().clear();
            for (int i = 0; i < data.days.length; i++) {
                pieChart.getData().add(new PieChart.Data(data.days[i], data.weeklyData[i]));
            }
        }
    }

    @FXML
    private void handleFilter() {
        ContextMenu menu = (ContextMenu) btnFilter.getUserData();
        if (menu != null) menu.show(btnFilter, Side.BOTTOM, 0, 0);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/Statistik/statistik.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) headerLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            ((Stage) headerLabel.getScene().getWindow()).close();
        }
    }

    private static class PlatformData {
        String name, averageTime, iconFileName, color;
        String[] days;
        double[] weeklyData;
        PlatformData(String name, String averageTime, String iconFileName,
                     String[] days, double[] weeklyData, String color) {
            this.name = name;
            this.averageTime = averageTime;
            this.iconFileName = iconFileName;
            this.days = days;
            this.weeklyData = weeklyData;
            this.color = color;
        }
    }
}
