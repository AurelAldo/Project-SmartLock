package XML;

import org.w3c.dom.*;
import Model.Users;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XMLUserHelper {

    // Lokasi file: <project_root>/data/users.xml
    private static final String FILE_PATH;

    static {
        String baseDir = System.getProperty("user.dir"); // lokasi root project
        FILE_PATH = baseDir + File.separator + "data" + File.separator + "users.xml";
        System.out.println("XMLUserHelper FILE_PATH: " + FILE_PATH);
    }

    // Pastikan file XML ada
    public static void ensureXMLExists() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("users");
                doc.appendChild(rootElement);

                write(doc);
                System.out.println("users.xml baru dibuat di: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tambah user
    public static void addUser(String username, String password) {
        try {
            ensureXMLExists();
            Document doc = loadXML();
            Element root = doc.getDocumentElement();

            Element user = doc.createElement("user");
            Element uname = doc.createElement("username");
            uname.appendChild(doc.createTextNode(username));
            user.appendChild(uname);

            Element pass = doc.createElement("password");
            pass.appendChild(doc.createTextNode(password));
            user.appendChild(pass);

            root.appendChild(user);
            write(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(Users user) {
        addUser(user.getUsername(), user.getPassword());
    }

    // Validasi login
    public static boolean validateLogin(String username, String password) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return false;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList userList = doc.getElementsByTagName("user");
            for (int i = 0; i < userList.getLength(); i++) {
                Element user = (Element) userList.item(i);
                String storedUsername = user.getElementsByTagName("username").item(0).getTextContent();
                String storedPassword = user.getElementsByTagName("password").item(0).getTextContent();
                if (storedUsername.equals(username) && storedPassword.equals(password)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Load XML
    private static Document loadXML() throws Exception {
        File file = new File(FILE_PATH);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(file);
    }

    // Write XML
    private static void write(Document doc) throws TransformerException, IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
