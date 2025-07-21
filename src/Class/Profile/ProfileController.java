package Class.Profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import Model.ProfileStatsManager;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML private ImageView avatarImage;
    @FXML private Label userNameLabel;
    @FXML private Label levelLabel;
    @FXML private Label xpLabel;

    @FXML private Label percentStatLabel;
    @FXML private Label weeklyTasksLabel;
    @FXML private Label totalTasksLabel;

    @FXML private Label rewardPercentLabel;
    @FXML private ProgressBar rewardProgressBar;

    @FXML private TilePane achievementTilePane;

    @FXML private Label pointsLabel;
    @FXML private VBox rewardBox;
    @FXML private Label namaReward1;
    @FXML private Label pointreward1;
    @FXML private Label namaReward2;
    @FXML private Label pointreward2;

    @FXML
    private void initialize() {
        userNameLabel.setText("User");

        refreshAll();
    }

    public void refreshAll() {
        refreshStats();
        refreshAchievements();
    }

    private void refreshStats() {
        int totalTasks   = ProfileStatsManager.getTotalTasksCompleted();
        int weeklyTasks  = ProfileStatsManager.getWeeklyTasksCompleted();
        int taskStreak   = ProfileStatsManager.getTaskStreak();

        int totalFocus   = ProfileStatsManager.getTotalFocusDays();
        int focusStreak  = ProfileStatsManager.getFocusStreak();

        int points       = ProfileStatsManager.getPoints();
        int xp           = ProfileStatsManager.getXp();
        int level        = ProfileStatsManager.getLevel();
        int xpIntoLevel  = ProfileStatsManager.getXpIntoLevel();
        int xpCap        = ProfileStatsManager.getXpForCurrentLevelCap();

        totalTasksLabel.setText(String.valueOf(totalTasks));
        weeklyTasksLabel.setText(weeklyTasks + "/10");
        percentStatLabel.setText((int)Math.min(100,(weeklyTasks/10.0*100)) + "%");

        levelLabel.setText("Level " + level);
        xpLabel.setText("XP: " + xpIntoLevel + "/" + xpCap);

        double p = xpCap == 0 ? 0 : (xpIntoLevel / (double) xpCap);
        rewardProgressBar.setProgress(p);
        rewardPercentLabel.setText((int)(p*100) + "%");
        pointsLabel.setText(String.valueOf(points));

        if (namaReward1 != null) namaReward1.setText("Profile Khusus");
        if (pointreward1 != null) pointreward1.setText("100");

        if (namaReward2 != null) namaReward2.setText("Badge Baru");
        if (pointreward2 != null) pointreward2.setText("200");
    }

    // ------------------------------------
    // Achievements
    // ------------------------------------
    private void refreshAchievements() {
        // Bersih dulu, lalu build ulang tiles sesuai status file Achievement.xml
        achievementTilePane.getChildren().clear();
        List<AchievementManager.Achievement> list = AchievementManager.getAll();
        for (AchievementManager.Achievement a : list) {
            Node tile = buildAchievementNode(a.name, a.unlocked);
            achievementTilePane.getChildren().add(tile);
        }
    }

    /** Bikin node kecil untuk Grid Achievement (pakai Label stack sederhana). */
    private Node buildAchievementNode(String name, boolean unlocked) {
        Label icon = new Label(unlocked ? "🏅" : "🔒");
        icon.getStyleClass().add("achievement-icon");

        Label label = new Label(name);
        label.setWrapText(true);
        label.getStyleClass().add("achievement-label");

        VBox box = new VBox(4);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.getChildren().addAll(icon, label);
        box.getStyleClass().add("achievement-item");
        box.getStyleClass().add(unlocked ? "unlocked" : "locked");
        return box;
    }

    // ------------------------------------
    // Tombol Reward (Claim)
    // ------------------------------------
    @FXML
    private void buttonClaimReward() {
        int cost = parseIntSafe(pointreward1.getText(), 100);
        attemptClaim(namaReward1.getText(), cost);
    }

    @FXML
    private void buttonCandleRandom() {
        int cost = parseIntSafe(pointreward2.getText(), 200);
        attemptClaim(namaReward2.getText(), cost);
    }

    private void attemptClaim(String rewardName, int cost) {
        boolean ok = ProfileStatsManager.spendPoints(cost);
        if (ok) {
            showInfo("Reward berhasil di-claim: " + rewardName);
            refreshStats();
            // TODO: lakukan sesuatu (unlock wallpaper, dsb)
        } else {
            showInfo("Poin tidak cukup.");
        }
    }

    private int parseIntSafe(String s, int def) {
        if (s == null || s.isBlank()) return def;
        try { return Integer.parseInt(s.trim()); } catch (Exception ignored) {}
        return def;
    }

    // ------------------------------------
    // Setting (ikon gear)
    // ------------------------------------
    @FXML
    private void Setting() {
        showInfo("Menu Settings belum dibuat.");
    }

    // ------------------------------------
    // Util Alert
    // ------------------------------------
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void tomHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomLock(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/LockApp/Selector.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/TaskTrack/TaskTrack.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomProfile(ActionEvent event) throws IOException {
        
        System.out.println("Sudah di halaman Profile");
    }

}
