package Class.TaskControl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TaskTrackController {

    @FXML private VBox TugasDeadline;
    @FXML private VBox TugasQueue;

    private static final DateTimeFormatter DATE_FMT = XML.XMLHelper.getFormatter();

    public TaskTrackController() {
        TaskTrackerHolder.setInstance(this);
    }

    @FXML
    public void initialize() {
        reload();
    }

    public void reload() {
        TugasDeadline.getChildren().clear();
        TugasQueue.getChildren().clear();
        loadFromXML();
    }

    private void loadFromXML() {
        try {
            Document doc = XML.XMLHelper.getDocument();
            if (doc == null) return;
            NodeList list = doc.getElementsByTagName("Task");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String nama = el.getElementsByTagName("Nama").item(0).getTextContent();
                String deadline = el.getElementsByTagName("Deadline").item(0).getTextContent();
                renderTask(nama, deadline);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void TambahTugas1(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TaskTrack/TambahTask.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tambah Tugas");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void renderTask(String nama, String deadlineStr) {
        try {
            long sisaHari = XML.XMLHelper.sisaHari(deadlineStr);

            // Queue item
            FXMLLoader queueLoader = new FXMLLoader(getClass().getResource("/View/TaskTrack/HalamanTaskQueue.fxml"));
            Parent queueNode = queueLoader.load();
            HalamanTaskQueueController queueController = queueLoader.getController();
            queueController.initFor(nama, deadlineStr, this);

            String countdown;
            if (sisaHari < 0) countdown = "Lewat " + Math.abs(sisaHari) + "d";
            else if (sisaHari == 0) countdown = "Hari ini";
            else countdown = sisaHari + "d";

            queueController.setData(nama, countdown);
            TugasQueue.getChildren().add(queueNode);

            // Deadline panel (≤ 2 hari)
            if (sisaHari <= 2) {
                FXMLLoader deadlineLoader = new FXMLLoader(getClass().getResource("/View/TaskTrack/ItemTugas.fxml"));
                Parent deadlineNode = deadlineLoader.load();
                ItemTugasController itemController = deadlineLoader.getController();
                itemController.setParent(this);
                itemController.setData(nama, deadlineStr);
                TugasDeadline.getChildren().add(deadlineNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTaskUpdated() {
        reload();
    }

    public void onTaskDeleted() {
        reload();
    }

    @FXML
    private void tomHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        
    }

    @FXML
    private void tomLock(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/LockApp/Selector.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void tomTask(ActionEvent event) throws IOException {
        System.out.println("Sudah di halaman Task");
    }

    @FXML
    private void tomProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Profile/profile.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
