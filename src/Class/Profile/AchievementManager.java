package Class.Profile;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class AchievementManager {

    private static final String FILE = "Model/Achievement.xml";

    /** Struktur internal achievement. */
    public static class Achievement {
        public final String name;
        public final String type;  // "task" / "lock" / bebas
        public boolean unlocked;

        public Achievement(String name, String type, boolean unlocked) {
            this.name = name;
            this.type = type;
            this.unlocked = unlocked;
        }
    }

    /** Cache di memory. */
    private static final Map<String, Achievement> CACHE = new LinkedHashMap<>();
    private static boolean loaded = false;

    // ------------------------------------
    // Load / Save
    // ------------------------------------
    private static void ensureLoaded() {
        if (loaded) return;
        load();
        loaded = true;
    }

    private static void load() {
        CACHE.clear();
        File f = new File(FILE);
        if (!f.exists()) {
            createDefaultFile();
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            NodeList list = doc.getElementsByTagName("Achievement");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                String name = e.getAttribute("name");
                String type = e.getAttribute("type");
                boolean unlocked = Boolean.parseBoolean(e.getAttribute("unlocked"));
                CACHE.put(name, new Achievement(name, type, unlocked));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createDefaultFile() {
        // nilai default awal
        CACHE.put(ProfileEvents.ACH_TASK_STARTER, new Achievement(ProfileEvents.ACH_TASK_STARTER, "task", false));
        CACHE.put(ProfileEvents.ACH_TASK_STREAK7, new Achievement(ProfileEvents.ACH_TASK_STREAK7, "task", false));
        CACHE.put(ProfileEvents.ACH_FOCUS_DAY,    new Achievement(ProfileEvents.ACH_FOCUS_DAY,    "lock", false));
        CACHE.put(ProfileEvents.ACH_FOCUS_WEEK,   new Achievement(ProfileEvents.ACH_FOCUS_WEEK,   "lock", false));
        save();
    }

    private static void save() {
        try {
            DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
            DocumentBuilder db          = dbf.newDocumentBuilder();
            Document doc                = db.newDocument();
            Element root                = doc.createElement("Achievements");
            doc.appendChild(root);

            for (Achievement a : CACHE.values()) {
                Element el = doc.createElement("Achievement");
                el.setAttribute("name", a.name);
                el.setAttribute("type", a.type);
                el.setAttribute("unlocked", Boolean.toString(a.unlocked));
                root.appendChild(el);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(new File(FILE)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------
    // API
    // ------------------------------------
    public static List<Achievement> getAll() {
        ensureLoaded();
        return new ArrayList<>(CACHE.values());
    }

    public static boolean isUnlocked(String name) {
        ensureLoaded();
        Achievement a = CACHE.get(name);
        return a != null && a.unlocked;
    }

    public static void unlock(String name) {
        ensureLoaded();
        Achievement a = CACHE.get(name);
        if (a != null && !a.unlocked) {
            a.unlocked = true;
            save();
        }
    }

    public static void resetAll() {
        loaded = false;
        createDefaultFile();
        save();
    }
}
