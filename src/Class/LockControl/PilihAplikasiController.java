package Class.LockControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Model.LockModels;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class PilihAplikasiController {

    private static final String PATH_APPCARD = "/View/LockApp/AppCard.fxml";
    private static final String PATH_POPUP   = "/View/LockApp/popupaturwaktu.fxml";
    private static final String PATH_SELECTOR= "/View/LockApp/Selector.fxml";

    @FXML private VBox vboxcont;
    @FXML private Button conf; // onAction=\"#handleConfirm\"

    private final Map<String, LockModels.AppLockSetting> selected = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        loadAvailable();
    }

    private void loadAvailable() {
        if (vboxcont == null) return;
        vboxcont.getChildren().clear();
        List<LockModels.AppInfo> apps = LockModels.LockDataStore.loadAvailableApps();

        for (LockModels.AppInfo info : apps) {
            try {
                FXMLLoader ldr = new FXMLLoader(
                        Objects.requireNonNull(getClass().getResource(PATH_APPCARD),
                                "AppCard.fxml not found at " + PATH_APPCARD));
                Parent card = ldr.load();
                AppCardController ctrl = ldr.getController();

                LockModels.AppLockSetting already = selected.get(info.getId());
                Duration lim = already == null ? Duration.ZERO : already.getDailyLimit();

                ctrl.configureSelectable(info, lim, this::openPopupFor);

                vboxcont.getChildren().add(card);
            } catch (IOException | NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openPopupFor(LockModels.AppInfo appInfo) {
        try {
            FXMLLoader ldr = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(PATH_POPUP),
                            "popupaturwaktu.fxml not found at " + PATH_POPUP));
            Parent root = ldr.load();
            PopupAturWaktuController ctrl = ldr.getController();
            LockModels.AppLockSetting existing = selected.get(appInfo.getId());

            ctrl.setContext(appInfo, existing, (info, setting) -> {
                selected.put(info.getId(), setting);
                loadAvailable(); // refresh card -> Edit
            });

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Atur Waktu");
            popup.setScene(new Scene(root));
            popup.showAndWait();
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleConfirm(ActionEvent e) {
    LockModels.LockDataStore.saveLockedApps(selected.values());
    goToSelector(e);
}


    @FXML
    private void backButton1(ActionEvent e) {
        goToSelector(e);
    }

    private void goToSelector(ActionEvent e) {
        try {
            FXMLLoader ldr = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(PATH_SELECTOR),
                            "Selector.fxml not found at " + PATH_SELECTOR));
            Parent root = ldr.load();
            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
            st.setScene(new Scene(root));
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void importExistingLocked(List<LockModels.AppLockSetting> locked) {
        selected.clear();
        if (locked != null) {
            for (LockModels.AppLockSetting setting : locked) {
                selected.put(setting.getApp().getId(), setting);
            }
        }
        loadAvailable();
    }
}
