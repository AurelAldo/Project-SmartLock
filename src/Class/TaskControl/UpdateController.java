package Class.TaskControl;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateController {

    @FXML private TextField editNama;
    @FXML private DatePicker editDeadline;

    private String oldNama;
    private String oldDeadline;
    private TaskTrackController parent;

    private static final DateTimeFormatter FMT = XML.XMLHelper.getFormatter();

    public void setOriginalData(String nama, String deadline, TaskTrackController parent) {
        this.oldNama = nama;
        this.oldDeadline = deadline;
        this.parent = parent;
        editNama.setText(nama);
        try {
            editDeadline.setValue(LocalDate.parse(deadline, FMT));
        } catch (Exception e) {
            editDeadline.setValue(LocalDate.now());
        }
    }

    @FXML
    private void saveEdit() {
        String newNama = editNama.getText();
        if (newNama == null || newNama.isBlank()) {
            newNama = oldNama;
        }
        String newDeadline = (editDeadline.getValue() != null)
                ? editDeadline.getValue().format(FMT)
                : oldDeadline;

        XML.XMLHelper.updateTask(oldNama, newNama, newDeadline);
        if (parent != null) parent.onTaskUpdated();
        close();
    }

    @FXML
    private void candelEdit() {
        close();
    }

    @FXML
    private void deleteTask() {
        // Hapus task lama
        XML.XMLHelper.deleteTaskByName(oldNama);
        if (parent != null) parent.onTaskDeleted();
        close();
    }

    private void close() {
        Stage st = (Stage) editNama.getScene().getWindow();
        st.close();
    }
}
