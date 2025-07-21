package Model;

import javafx.scene.image.Image;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.*;

public final class LockModels {

    private LockModels() {}
    private static final String LOCK_FILE = "src/Model/LockApp.xml";

    public static class AppInfo {
        private final String id;
        private final String displayName;
        private final Image icon;
        private Duration currentUsage = Duration.ZERO;

        public AppInfo(String id, String displayName, Image icon) {
            this.id = id;
            this.displayName = displayName;
            this.icon = icon;
        }
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Image getIcon() { return icon; }
        public Duration getCurrentUsage() { return currentUsage; }
        public void setCurrentUsage(Duration d) { currentUsage = d == null ? Duration.ZERO : d; }
    }

    public static class AppLockSetting {
        private final AppInfo app;
        private Duration dailyLimit;
        private EnumSet<DayOfWeek> activeDays;

        public AppLockSetting(AppInfo app, Duration dailyLimit, EnumSet<DayOfWeek> activeDays) {
            this.app = Objects.requireNonNull(app, "app");
            this.dailyLimit = dailyLimit == null ? Duration.ZERO : dailyLimit;
            this.activeDays = (activeDays == null || activeDays.isEmpty())
                    ? EnumSet.allOf(DayOfWeek.class)
                    : EnumSet.copyOf(activeDays);
        }
        public AppInfo getApp() { return app; }
        public Duration getDailyLimit() { return dailyLimit; }
        public void setDailyLimit(Duration d) { dailyLimit = d == null ? Duration.ZERO : d; }
        public EnumSet<DayOfWeek> getActiveDays() { return activeDays; }
        public void setActiveDays(EnumSet<DayOfWeek> d) {
            activeDays = (d == null || d.isEmpty()) ? EnumSet.allOf(DayOfWeek.class) : EnumSet.copyOf(d);
        }
    }

    public static class LockDataStore {
        private LockDataStore() {}

        private static final Map<String, AppInfo> AVAILABLE = new LinkedHashMap<>();
        private static final Map<String, AppLockSetting> LOCKED = new LinkedHashMap<>();

        static {
            registerDefaultApps();
            loadFromXML();
        }

        public static void registerDefaultApps() {
            if (!AVAILABLE.isEmpty()) return;
            registerAvailableApp("ig", "Instagram", "/AsetSB/instagram.png");
            registerAvailableApp("yt", "YouTube", "/AsetSB/youtuve.png");
            registerAvailableApp("mlbb", "MLBB", "/AsetSB/mlbb.jpg");
            registerAvailableApp("spotify", "Spotify", "/AsetSB/spotify.png");
            registerAvailableApp("tiktok", "TikTok", "/AsetSB/tiktok.png");
        }

        public static void registerAvailableApp(String id, String name, String iconResPath) {
            AVAILABLE.put(id, new AppInfo(id, name, loadIcon(iconResPath)));
        }

        public static void clearAvailableApps() {
            AVAILABLE.clear();
        }

        public static List<AppInfo> loadAvailableApps() {
            return new ArrayList<>(AVAILABLE.values());
        }

        public static List<AppLockSetting> loadLockedApps() {
            return new ArrayList<>(LOCKED.values());
        }

        public static void saveLockedApps(Collection<AppLockSetting> settings) {
            LOCKED.clear();
            if (settings != null) {
                for (AppLockSetting s : settings) {
                    LOCKED.put(s.getApp().getId(), s);
                }
            }
            saveToXML();
        }

        public static void putLockedApp(AppLockSetting setting) {
            LOCKED.put(setting.getApp().getId(), setting);
            saveToXML();
        }

        public static void removeLockedApp(String appId) {
            LOCKED.remove(appId);
            saveToXML();
        }

        public static AppLockSetting getLockedSetting(String appId) {
            return LOCKED.get(appId);
        }

        public static boolean isLocked(String appId) {
            return LOCKED.containsKey(appId);
        }

        private static void saveToXML() {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element root = doc.createElement("lockedApps");
                doc.appendChild(root);

                for (AppLockSetting setting : LOCKED.values()) {
                    Element appEl = doc.createElement("app");
                    appEl.setAttribute("id", setting.getApp().getId());
                    appEl.setAttribute("limit", String.valueOf(setting.getDailyLimit().toMinutes()));

                    // days
                    StringBuilder days = new StringBuilder();
                    for (DayOfWeek day : setting.getActiveDays()) {
                        if (days.length() > 0) days.append(",");
                        days.append(day.name());
                    }
                    appEl.setAttribute("days", days.toString());

                    root.appendChild(appEl);
                }

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(LOCK_FILE));
                transformer.transform(source, result);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private static void loadFromXML() {
            try {
                File file = new File(LOCK_FILE);
                if (!file.exists()) return;

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

                NodeList list = doc.getElementsByTagName("app");
                LOCKED.clear();

                for (int i = 0; i < list.getLength(); i++) {
                    Element appEl = (Element) list.item(i);
                    String id = appEl.getAttribute("id");
                    long minutes = Long.parseLong(appEl.getAttribute("limit"));
                    Duration limit = Duration.ofMinutes(minutes);

                    EnumSet<DayOfWeek> days = EnumSet.allOf(DayOfWeek.class);
                    String daysAttr = appEl.getAttribute("days");
                    if (daysAttr != null && !daysAttr.isBlank()) {
                        days = EnumSet.noneOf(DayOfWeek.class);
                        for (String d : daysAttr.split(",")) {
                            try {
                                days.add(DayOfWeek.valueOf(d.trim()));
                            } catch (Exception ignored) {}
                        }
                    }

                    AppInfo app = AVAILABLE.get(id);
                    if (app != null) {
                        LOCKED.put(id, new AppLockSetting(app, limit, days));
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private static Image loadIcon(String resPath) {
            if (resPath == null || resPath.isBlank()) return null;
            URL url = LockDataStore.class.getResource(resPath);
            if (url == null) {
                System.err.println("[LockDataStore] Icon NOT FOUND: " + resPath);
                return null;
            }
            try {
                return new Image(url.toExternalForm(), true);
            } catch (Exception ex) {
                System.err.println("[LockDataStore] Failed load icon: " + resPath + " -> " + ex);
                return null;
            }
        }
    }
}
