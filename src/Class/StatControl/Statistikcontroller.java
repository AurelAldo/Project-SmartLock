package Class.StatControl;

import javafx.event.ActionEvent;
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
import javafx.stage.Stage;
import javafx.geometry.Side;

import java.io.IOException;

public class Statistikcontroller {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private Button btnFilter;
    @FXML
    private Button btnFilter1;
    @FXML
    private Button btnInstagram;
    @FXML 
    private Label StatPenggunaan1;
    @FXML 
    private Label StatPenggunaan2;
    @FXML 
    private Label StatPenggunaan3;
    @FXML 
    private Label StatPenggunaan4;

    private enum ChartType { BAR_CHART, PIE_CHART }
    private enum TimePeriod { HARI_INI, MINGGU_INI }

    private ChartType currentChartType = ChartType.BAR_CHART;
    private TimePeriod currentTimePeriod = TimePeriod.HARI_INI;

    @FXML
    public void initialize() {
        setupBarChart();
        setupPieChart();
        setupFilterDropdown();
        setupTimeDropdown();
        showChart(ChartType.BAR_CHART);
    }

    private void setupFilterDropdown() {
        ContextMenu menu = new ContextMenu();
        MenuItem bar = new MenuItem("Bar Chart");
        bar.setOnAction(e -> {
            currentChartType = ChartType.BAR_CHART;
            showChart(ChartType.BAR_CHART);
            btnFilter.setText("Bar Chart");
            updateChartData(currentTimePeriod);
        });
        MenuItem pie = new MenuItem("Pie Chart");
        pie.setOnAction(e -> {
            currentChartType = ChartType.PIE_CHART;
            showChart(ChartType.PIE_CHART);
            btnFilter.setText("Pie Chart");
            updateChartData(currentTimePeriod);
        });
        menu.getItems().addAll(bar, pie);
        btnFilter.setUserData(menu);
    }

    private void setupTimeDropdown() {
        ContextMenu menu = new ContextMenu();
        MenuItem hariIni = new MenuItem("Hari Ini");
        hariIni.setOnAction(e -> {
            currentTimePeriod = TimePeriod.HARI_INI;
            btnFilter1.setText("Hari Ini");
            updateChartData(currentTimePeriod);
        });
        MenuItem mingguIni = new MenuItem("Minggu Ini");
        mingguIni.setOnAction(e -> {
            currentTimePeriod = TimePeriod.MINGGU_INI;
            btnFilter1.setText("Minggu Ini");
            updateChartData(currentTimePeriod);
        });
        menu.getItems().addAll(hariIni, mingguIni);
        btnFilter1.setUserData(menu);
    }

    private void setupBarChart() {
        barChart.setTitle("Penggunaan Hari Ini");
        updateBarChartData(TimePeriod.HARI_INI);
    }

    private void setupPieChart() {
        pieChart.setTitle("Penggunaan Hari Ini");
        updatePieChartData(TimePeriod.HARI_INI);
    }

    private void showChart(ChartType type) {
        boolean bar = type == ChartType.BAR_CHART;
        barChart.setVisible(bar);
        pieChart.setVisible(!bar);
    }

    private void updateChartData(TimePeriod period) {
        if (currentChartType == ChartType.BAR_CHART) {
            updateBarChartData(period);
        } else {
            updatePieChartData(period);
        }
        updateLabels(period);
    }

    private void updateBarChartData(TimePeriod period) {
        barChart.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("jam");
        if (period == TimePeriod.HARI_INI) {
            s.getData().add(new XYChart.Data<>("Instagram", 1.9));
            s.getData().add(new XYChart.Data<>("TikTok", 3.6));
            s.getData().add(new XYChart.Data<>("YouTube", 0.4)); // ditukar dengan MLBB
            s.getData().add(new XYChart.Data<>("MLBB", 2.1));    // ditukar dengan YouTube
            barChart.setTitle("Penggunaan Hari Ini");
        } else {
            s.getData().add(new XYChart.Data<>("Instagram", 18.3));
            s.getData().add(new XYChart.Data<>("TikTok", 25.5));
            s.getData().add(new XYChart.Data<>("YouTube", 8.5)); // ditukar dengan MLBB
            s.getData().add(new XYChart.Data<>("MLBB", 13.5));   // ditukar dengan YouTube
            barChart.setTitle("Penggunaan Minggu Ini");
        }
        barChart.getData().add(s);
        barChart.getXAxis().setTickLabelRotation(-45);
    }

    private void updatePieChartData(TimePeriod period) {
        pieChart.getData().clear();
        if (period == TimePeriod.HARI_INI) {
            pieChart.getData().add(new PieChart.Data("Instagram", 1.9));
            pieChart.getData().add(new PieChart.Data("TikTok", 3.6));
            pieChart.getData().add(new PieChart.Data("YouTube", 0.4)); // ditukar
            pieChart.getData().add(new PieChart.Data("MLBB", 2.1));    // ditukar
            pieChart.setTitle("Penggunaan Hari Ini");
        } else {
            pieChart.getData().add(new PieChart.Data("Instagram", 18.3));
            pieChart.getData().add(new PieChart.Data("TikTok", 25.5));
            pieChart.getData().add(new PieChart.Data("YouTube", 8.5)); // ditukar
            pieChart.getData().add(new PieChart.Data("MLBB", 13.5));   // ditukar
            pieChart.setTitle("Penggunaan Minggu Ini");
        }
    }

    private void updateLabels(TimePeriod period) {
        if (period == TimePeriod.HARI_INI) {
            StatPenggunaan1.setText("2j 0m");
            StatPenggunaan2.setText("1j 30m");
            StatPenggunaan3.setText("3j 30m");
            StatPenggunaan4.setText("2j 30m");
        } else {
            StatPenggunaan1.setText("14j 0m");
            StatPenggunaan2.setText("10j 30m");
            StatPenggunaan3.setText("24j 30m");
            StatPenggunaan4.setText("17j 30m");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleFilter() {
        ContextMenu m = (ContextMenu) btnFilter.getUserData();
        if (m != null) {
            m.show(btnFilter, Side.BOTTOM, 0, 0);
        }
    }

    @FXML
    private void filterhari() {
        ContextMenu m = (ContextMenu) btnFilter1.getUserData();
        if (m != null) {
            m.show(btnFilter1, Side.BOTTOM, 0, 0);
        }
    }

    private void navigateToDetail(String platform) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/Statistik/statistik3.fxml"));
            Parent root = loader.load();
            PlatformDetailController controller = loader.getController();
            controller.setPlatform(platform);
            Stage stage = (Stage) btnInstagram.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading detail page for " + platform);
        }
    }

    @FXML
    private void tombolinstagram() { navigateToDetail("Instagram"); }
    @FXML
    private void tomboltiktok()    { navigateToDetail("TikTok"); }
    @FXML
    private void tombolmlbb()      { navigateToDetail("MLBB"); }   // ganti dari Twitter
    @FXML
    private void tombolyoutube()   { navigateToDetail("YouTube"); }
}
