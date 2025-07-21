package Class.LockControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import Model.LockModels;
import Model.ProfileStatsManager;   // <- path baru sesuai file kamu

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SelectorController {

    private static final String PATH_APPCARD = "/View/LockApp/AppCard.fxml";
    private static final String PATH_PILIH   = "/View/LockApp/PilihAplikasi.fxml";

    @FXML private VBox appListContainer;

    @FXML
    private void initialize() {
        refreshList();

        // (Opsional) contoh cek fokus harian
        // Kalau semua locked apps usage == 0 -> catat fokus hari ini
        // (Kamu boleh hapus ini kalau deteksinya di tempat lain.)
        if (isAllLockedUnusedToday()) {
            ProfileStatsManager.onFocusDay();
        } else {
            // Kalau mau reset streak bila ada yg kepake: aktifkan baris ini
            // ProfileStatsManager.onFocusBroken();
        }
    }

    /** Refresh daftar aplikasi yang sedang di-lock. */
    private void refreshList() {
        if (appListContainer == null) return;
        appListContainer.getChildren().clear();
        List<LockModels.AppLockSetting> locked = LockModels.LockDataStore.loadLockedApps();

        for (LockModels.AppLockSetting setting : locked) {
            try {
                FXMLLoader ldr = new FXMLLoader(
                        Objects.requireNonNull(getClass().getResource(PATH_APPCARD),
                                "AppCard.fxml not found at " + PATH_APPCARD));
                Parent card = ldr.load();
                AppCardController ctrl = ldr.getController();

                ctrl.configureLocked(setting, info -> {
                    // User klik remove
                    LockModels.LockDataStore.removeLockedApp(info.getId());
                    refreshList();
                    // Tidak perlu update XP di sini kecuali kamu mau (lihat Opsi B di bawah).
                });

                appListContainer.getChildren().add(card);
            } catch (IOException | NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    // -------------------------------------------------------
    // OPSIONAL: Demo cek semua locked tidak dipakai → fokus
    // -------------------------------------------------------
    private boolean isAllLockedUnusedToday() {
        var locked = LockModels.LockDataStore.loadLockedApps();
        // Kalau belum track usage per hari, kita anggap fokus kalau ada yang locked.
        // Ganti dengan pengecekan real kalau sudah ada durasi pemakaian.
        return !locked.isEmpty(); // sementara: ada lock = niat fokus
    }

    // -------------------------------------------------------
    // NAVIGASI
    // -------------------------------------------------------
    @FXML
    private void tomHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomLock(ActionEvent event) {
        System.out.println("Sudah di halaman Lock");
    }

    @FXML
    private void tomTask(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/TaskTrack/TaskTrack.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Profile/profile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleAddButton(ActionEvent e) {
        goToPilih(e);
    }

    private void goToPilih(ActionEvent e) {
        try {
            FXMLLoader ldr = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(PATH_PILIH),
                            "PilihAplikasi.fxml not found at " + PATH_PILIH));
            Parent root = ldr.load();
            PilihAplikasiController ctrl = ldr.getController();
            // kirim data yg sudah locked supaya status di card -> Edit
            ctrl.importExistingLocked(LockModels.LockDataStore.loadLockedApps());

            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
            st.setScene(new Scene(root));
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
