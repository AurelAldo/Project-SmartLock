package Class.LockControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Model.LockModels;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.EnumSet;
import java.util.function.BiConsumer;

/**
 * Popup pengaturan limit waktu + hari aktif.
 * Dipanggil dari PilihAplikasiController.
 *
 * Gunakan setContext() SEBELUM ditampilkan.
 */
public class PopupAturWaktuController {

    @FXML private Spinner<Integer> spinnerJam;
    @FXML private Spinner<Integer> spinnerMenit;

    @FXML private CheckBox cbSenin;
    @FXML private CheckBox cbSelasa;
    @FXML private CheckBox cbRabu;
    @FXML private CheckBox cbKamis;
    @FXML private CheckBox cbJumat;
    @FXML private CheckBox cbSabtu;
    @FXML private CheckBox cbMinggu;

    // FXML: onAction=\"#handleSimpan\"
    @FXML private Button dummyForInjection; // not used, ensures class loaded

    // Data
    private LockModels.AppInfo appInfo;
    private LockModels.AppLockSetting existingSetting; // boleh null
    // Callback: (appInfo, newSetting)
    private BiConsumer<LockModels.AppInfo, LockModels.AppLockSetting> saveCallback;

    @FXML
    private void initialize() {
        spinnerJam.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        spinnerMenit.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    /** Diset oleh pemanggil. existing boleh null. */
    public void setContext(LockModels.AppInfo info,
                           LockModels.AppLockSetting existing,
                           BiConsumer<LockModels.AppInfo, LockModels.AppLockSetting> onSave) {
        this.appInfo = info;
        this.existingSetting = existing;
        this.saveCallback = onSave;
        applyExisting();
    }

    private void applyExisting() {
        if (existingSetting == null) return;
        long h = existingSetting.getDailyLimit().toHours();
        long m = existingSetting.getDailyLimit().minusHours(h).toMinutes();
        spinnerJam.getValueFactory().setValue((int) h);
        spinnerMenit.getValueFactory().setValue((int) m);

        EnumSet<DayOfWeek> d = existingSetting.getActiveDays();
        cbSenin.setSelected(d.contains(DayOfWeek.MONDAY));
        cbSelasa.setSelected(d.contains(DayOfWeek.TUESDAY));
        cbRabu.setSelected(d.contains(DayOfWeek.WEDNESDAY));
        cbKamis.setSelected(d.contains(DayOfWeek.THURSDAY));
        cbJumat.setSelected(d.contains(DayOfWeek.FRIDAY));
        cbSabtu.setSelected(d.contains(DayOfWeek.SATURDAY));
        cbMinggu.setSelected(d.contains(DayOfWeek.SUNDAY));
    }

    @FXML
    private void handleSimpan(ActionEvent e) {
        int h = spinnerJam.getValue();
        int m = spinnerMenit.getValue();
        Duration limit = Duration.ofHours(h).plusMinutes(m);

        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        if (cbSenin.isSelected()) days.add(DayOfWeek.MONDAY);
        if (cbSelasa.isSelected()) days.add(DayOfWeek.TUESDAY);
        if (cbRabu.isSelected()) days.add(DayOfWeek.WEDNESDAY);
        if (cbKamis.isSelected()) days.add(DayOfWeek.THURSDAY);
        if (cbJumat.isSelected()) days.add(DayOfWeek.FRIDAY);
        if (cbSabtu.isSelected()) days.add(DayOfWeek.SATURDAY);
        if (cbMinggu.isSelected()) days.add(DayOfWeek.SUNDAY);
        if (days.isEmpty()) {
            // Kalau user tidak pilih, anggap semua hari
            days = EnumSet.allOf(DayOfWeek.class);
        }

        LockModels.AppLockSetting newSetting =
                new LockModels.AppLockSetting(appInfo, limit, days);

        if (saveCallback != null) {
            saveCallback.accept(appInfo, newSetting);
        }

        // Tutup popup
        Node src = (Node) e.getSource();
        Stage st = (Stage) src.getScene().getWindow();
        st.close();
    }
}
