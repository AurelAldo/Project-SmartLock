package Class.TaskControl;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class HalamanTaskQueueController {

    @FXML private Label tugasname;
    @FXML private Label countDown;

    private String currentDeadline;
    private TaskTrackController parent;

    public void initFor(String nama, String deadline, TaskTrackController parent) {
        this.currentDeadline = deadline;
        this.parent = parent;
    }

    public void setData(String nama, String countdown) {
        tugasname.setText(nama);
        countDown.setText(countdown);
    }

    @FXML
    private void EditTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TaskTrack/Update.fxml"));
            Parent root = loader.load();
            UpdateController controller = loader.getController();
            controller.setOriginalData(tugasname.getText(), currentDeadline, parent);
            Stage stage = new Stage();
            stage.setTitle("Edit Tugas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
