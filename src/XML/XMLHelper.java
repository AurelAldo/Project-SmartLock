package XML;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class XMLHelper {

    private static final File DATA_FILE = new File(
            System.getProperty("user.home") + File.separator +
            "Smartlock" + File.separator + "TaskDB.xml");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static void ensureFile(DocumentBuilder builder) throws Exception {
        if (!DATA_FILE.getParentFile().exists()) {
            DATA_FILE.getParentFile().mkdirs();
        }
        if (!DATA_FILE.exists()) {
            Document doc = builder.newDocument();
            doc.appendChild(doc.createElement("Tasks"));
            saveDocument(doc);
        }
    }

    public static Document getDocument() {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            ensureFile(b);
            return b.parse(DATA_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveDocument(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer tr = tf.newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.transform(new DOMSource(doc), new StreamResult(DATA_FILE));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void addTask(String nama, String deadline) {
        try {
            Document doc = getDocument();
            if (doc == null) return;
            Element root = doc.getDocumentElement();

            Element task = doc.createElement("Task");
            Element enama = doc.createElement("Nama");
            enama.appendChild(doc.createTextNode(nama));
            Element edl = doc.createElement("Deadline");
            edl.appendChild(doc.createTextNode(deadline));

            task.appendChild(enama);
            task.appendChild(edl);
            root.appendChild(task);

            saveDocument(doc);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void deleteTaskByName(String nama) {
        try {
            Document doc = getDocument();
            if (doc == null) return;
            NodeList list = doc.getElementsByTagName("Task");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String nm = el.getElementsByTagName("Nama").item(0).getTextContent();
                if (nm.equals(nama)) {
                    el.getParentNode().removeChild(el);
                    break;
                }
            }
            saveDocument(doc);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void updateTask(String oldNama, String newNama, String newDeadline) {
        try {
            Document doc = getDocument();
            if (doc == null) return;
            NodeList list = doc.getElementsByTagName("Task");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String nm = el.getElementsByTagName("Nama").item(0).getTextContent();
                if (nm.equals(oldNama)) {
                    el.getElementsByTagName("Nama").item(0).setTextContent(newNama);
                    el.getElementsByTagName("Deadline").item(0).setTextContent(newDeadline);
                    break;
                }
            }
            saveDocument(doc);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Utility countdown (hari tersisa)
    public static long sisaHari(String deadline) {
        try {
            LocalDate d = LocalDate.parse(deadline, FMT);
            return ChronoUnit.DAYS.between(LocalDate.now(), d);
        } catch (Exception e) {
            return 9999;
        }
    }

    public static DateTimeFormatter getFormatter() {
        return FMT;
    }
    
}
