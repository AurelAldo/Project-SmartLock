package Class;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GoogleControl {

    @FXML
    private Button googleAccountBtn;

    @FXML
    public void logintoHome(ActionEvent event) {
        try {
            Stage popupStage = (Stage) googleAccountBtn.getScene().getWindow(); // popup
            Stage mainStage = (Stage) popupStage.getOwner(); // ambil owner (stage utama)

            Parent home = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
            mainStage.setScene(new Scene(home));
            popupStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
