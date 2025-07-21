package Class.TaskControl;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TambahTugasController {

    @FXML private TextField namaField;
    @FXML private DatePicker tanggalPicker;
    @FXML private CheckBox notifCheckbox; // opsional

    private static final DateTimeFormatter FMT = XML.XMLHelper.getFormatter();

    @FXML
    private void handleSimpan(ActionEvent e) {
        String nama = namaField.getText();
        if (nama == null || nama.isBlank()) {
            System.out.println("Nama tugas kosong.");
            return;
        }
        String deadline = (tanggalPicker.getValue() != null)
                ? tanggalPicker.getValue().format(FMT)
                : LocalDate.now().format(FMT);

        XML.XMLHelper.addTask(nama, deadline);

        TaskTrackController main = TaskTrackerHolder.getInstance();
        if (main != null) main.reload();

        Stage st = (Stage) namaField.getScene().getWindow();
        st.close();
    }
}
