package Class;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LogControl {

    @FXML private TextField pengguna1;
    @FXML private PasswordField pass1;
    @FXML private Button daftarid;
    @FXML private Button gugel;
    @FXML private Button loginsir;

    private final String DEFAULT_USER = "user";
    private final String DEFAULT_PASS = "1234";

    @FXML
    public void TombolDaftar(ActionEvent event) {
        go("/View/Signup.fxml", daftarid);
    }

    @FXML
    public void TombolLogin(ActionEvent event) {
        String user = pengguna1.getText().trim();
        String pass = pass1.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Data kurang", "Isi username & password.");
            return;
        }

        if (user.equals(DEFAULT_USER) && pass.equals(DEFAULT_PASS)) {
            go("/View/Home.fxml", loginsir);
            return;
        }

        alert(Alert.AlertType.ERROR, "Login gagal", "Username atau password salah.");
        // }
    }

    @FXML
    public void TombolGugel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/GLogin.fxml"));
            Parent popupRoot = loader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("Pilih Google Account");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initOwner(gugel.getScene().getWindow());
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Popup gagal", e.getMessage());
        }
    }

    private void go(String fxml, Button src) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) src.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Gagal buka halaman", e.getMessage());
        }
    }

    private void alert(Alert.AlertType type, String head, String msg) {
        Alert a = new Alert(type);
        a.setTitle("Login");
        a.setHeaderText(head);
        a.setContentText(msg);
        a.showAndWait();
    }
}
