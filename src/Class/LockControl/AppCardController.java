package Class.LockControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Model.LockModels;
import java.time.Duration;
import java.util.function.Consumer;

public class AppCardController {

    public enum Mode { SELECTABLE, LOCKED }

    @FXML private ImageView imageAset;
    @FXML private Label nameApp1;
    @FXML private Label waktupenggunaanharianApp;
    @FXML private Button actionBtn;

    private Mode mode = Mode.SELECTABLE;
    private LockModels.AppInfo appInfo;
    private Duration configuredLimit = Duration.ZERO;

    // Callback ketika user klik tombol (Add/Edit) di mode SELECTABLE
    private Consumer<LockModels.AppInfo> onConfigureRequested;

    // Callback ketika user klik tombol (Remove) di mode LOCKED
    private Consumer<LockModels.AppInfo> onRemoveRequested;

    /** Dipanggil oleh parent (PilihAplikasiController / SelectorController) sesudah load FXML. */
    public void configureSelectable(LockModels.AppInfo info,
                                    Duration currentLimit,
                                    Consumer<LockModels.AppInfo> configureCallback) {
        this.mode = Mode.SELECTABLE;
        this.appInfo = info;
        this.onConfigureRequested = configureCallback;
        this.onRemoveRequested = null;
        this.configuredLimit = currentLimit == null ? Duration.ZERO : currentLimit;
        refreshView();
    }

    /** Dipanggil parent saat menampilkan daftar locked. */
    public void configureLocked(LockModels.AppLockSetting setting,
                                Consumer<LockModels.AppInfo> removeCallback) {
        this.mode = Mode.LOCKED;
        this.appInfo = setting.getApp();
        this.onConfigureRequested = null;
        this.onRemoveRequested = removeCallback;
        this.configuredLimit = setting.getDailyLimit();
        refreshView();
    }

    private void refreshView() {
        if (appInfo == null) return;
        if (nameApp1 != null) {
            nameApp1.setText(appInfo.getDisplayName());
        }

        // Icon (kalau ada)
        Image ic = appInfo.getIcon();
        if (ic != null && imageAset != null) {
            imageAset.setImage(ic);
        }

        // Teks usage / limit
        if (waktupenggunaanharianApp != null) {
            if (configuredLimit != null && !configuredLimit.isZero()) {
                long h = configuredLimit.toHours();
                long m = configuredLimit.minusHours(h).toMinutes();
                waktupenggunaanharianApp.setText(String.format("Limit: %02d:%02d /hari", h, m));
            } else {
                waktupenggunaanharianApp.setText("Belum diatur");
            }
        }

        // Tombol
        if (actionBtn != null) {
            if (mode == Mode.SELECTABLE) {
                actionBtn.setText(configuredLimit.isZero() ? "Add" : "Edit");
                actionBtn.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white;");
            } else {
                actionBtn.setText("X"); // remove
                actionBtn.setStyle("-fx-background-color: #ff4d4f; -fx-text-fill: white;");
            }
        }
    }

    @FXML
    private void AddButton(ActionEvent e) {
        if (mode == Mode.SELECTABLE) {
            if (onConfigureRequested != null) {
                onConfigureRequested.accept(appInfo);
            }
        } else { // LOCKED
            if (onRemoveRequested != null) {
                onRemoveRequested.accept(appInfo);
            }
        }
    }
}
