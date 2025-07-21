package Class.TaskControl;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import Model.ProfileStatsManager;

public class ItemTugasController {

    @FXML private Label namaTugasX;
    @FXML private Label tambahDeadlineX;
    @FXML private HBox root;

    private TaskTrackController parent;

    public void setParent(TaskTrackController parent) {
        this.parent = parent;
    }

    public void setData(String namaTugas, String deadline) {
        namaTugasX.setText(namaTugas);
        tambahDeadlineX.setText(deadline);
    }

    @FXML
    private void doneTask() {
    String nama = namaTugasX.getText();
    XML.XMLHelper.deleteTaskByName(nama);

    ProfileStatsManager.onTaskCompleted();

    if (parent != null) parent.onTaskDeleted();
}
}
