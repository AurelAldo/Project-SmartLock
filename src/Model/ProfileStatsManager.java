package Model;

import XML.XMLHelper;
import java.io.File;
import java.time.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import Class.Profile.AchievementManager;
import Class.Profile.ProfileEvents;

public final class ProfileStatsManager {
    private ProfileStatsManager(){}

    private static final String FILE = "Model/ProfileStats.xml";

    private static int totalTasksCompleted = 0;
    private static int weeklyTasksCompleted = 0;
    private static int taskStreak = 0;

    private static int totalFocusDays = 0;
    private static int focusStreak = 0;

    private static LocalDate lastTaskCompletionDate = null;
    private static LocalDate lastFocusDate = null;

    private static int points = 0;
    private static int xp = 0;

    private static boolean loaded = false;

    private static void ensureLoaded() {
        if (loaded) return;
        load();
        loaded = true;
    }

    private static void load() {
        File f = new File(FILE);
        if (!f.exists()) {
            save(); // create empty default
            return;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db         = dbf.newDocumentBuilder();
            Document doc               = db.parse(f);
            Element root               = doc.getDocumentElement();
            if (root == null) return;

            totalTasksCompleted   = getIntAttr(root, "totalTasksCompleted", 0);
            weeklyTasksCompleted  = getIntAttr(root, "weeklyTasksCompleted",0);
            taskStreak            = getIntAttr(root, "taskStreak",0);

            totalFocusDays        = getIntAttr(root, "totalFocusDays",0);
            focusStreak           = getIntAttr(root, "focusStreak",0);

            points                = getIntAttr(root, "points",0);
            xp                    = getIntAttr(root, "xp",0);

            lastTaskCompletionDate= getDateAttr(root, "lastTaskCompletionDate");
            lastFocusDate         = getDateAttr(root, "lastFocusDate");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void save() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db         = dbf.newDocumentBuilder();
            Document doc               = db.newDocument();
            Element root               = doc.createElement("ProfileStats");
            doc.appendChild(root);

            setAttr(root, "totalTasksCompleted", totalTasksCompleted);
            setAttr(root, "weeklyTasksCompleted", weeklyTasksCompleted);
            setAttr(root, "taskStreak", taskStreak);

            setAttr(root, "totalFocusDays", totalFocusDays);
            setAttr(root, "focusStreak", focusStreak);

            setAttr(root, "points", points);
            setAttr(root, "xp", xp);

            if (lastTaskCompletionDate != null) {
                root.setAttribute("lastTaskCompletionDate", lastTaskCompletionDate.toString());
            }
            if (lastFocusDate != null) {
                root.setAttribute("lastFocusDate", lastFocusDate.toString());
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(new File(FILE)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int getIntAttr(Element el, String name, int def) {
        String v = el.getAttribute(name);
        if (v == null || v.isBlank()) return def;
        try { return Integer.parseInt(v); } catch (Exception ignored) {}
        return def;
    }

    private static LocalDate getDateAttr(Element el, String name) {
        String v = el.getAttribute(name);
        if (v == null || v.isBlank()) return null;
        try { return LocalDate.parse(v); } catch (Exception ignored) {}
        return null;
    }

    private static void setAttr(Element el, String name, int v) {
        el.setAttribute(name, Integer.toString(v));
    }

    // ------------------------------------------------
    // Public baca nilai
    // ------------------------------------------------
    public static int getTotalTasksCompleted() {
        ensureLoaded();
        return totalTasksCompleted;
    }
    public static int getWeeklyTasksCompleted() {
        ensureLoaded();
        maybeResetWeekly();
        return weeklyTasksCompleted;
    }
    public static int getTaskStreak() {
        ensureLoaded();
        return taskStreak;
    }
    public static int getTotalFocusDays() {
        ensureLoaded();
        return totalFocusDays;
    }
    public static int getFocusStreak() {
        ensureLoaded();
        return focusStreak;
    }
    public static int getPoints() {
        ensureLoaded();
        return points;
    }
    public static int getXp() {
        ensureLoaded();
        return xp;
    }

    // Level dihitung dari XP (linear)
    public static int getLevel() {
        ensureLoaded();
        int level = 1;
        int xpNeededThisLevel = ProfileEvents.xpNeededForLevel(level);
        int remain = xp;
        while (remain >= xpNeededThisLevel) {
            remain -= xpNeededThisLevel;
            level++;
            xpNeededThisLevel = ProfileEvents.xpNeededForLevel(level);
        }
        return level;
    }
    public static int getXpIntoLevel() {
        int level = getLevel();
        int totalUsed = 0;
        for (int i = 1; i < level; i++) {
            totalUsed += ProfileEvents.xpNeededForLevel(i);
        }
        return xp - totalUsed;
    }
    public static int getXpForCurrentLevelCap() {
        return ProfileEvents.xpNeededForLevel(getLevel());
    }

    // ------------------------------------------------
    // Mutasi Stats dari event
    // ------------------------------------------------
    public static void onTaskCompleted() {
        ensureLoaded();
        LocalDate today = LocalDate.now();
        totalTasksCompleted++;
        weeklyTasksCompleted++;
        if (lastTaskCompletionDate == null || lastTaskCompletionDate.plusDays(1).equals(today)) {
            // lanjut streak
            taskStreak++;
        } else if (!today.equals(lastTaskCompletionDate)) {
            // putus
            taskStreak = 1;
        }
        lastTaskCompletionDate = today;

        addPoints(ProfileEvents.POINTS_PER_TASK);
        checkTaskAchievements();
        save();
    }

    public static void onFocusDay() {
        ensureLoaded();
        LocalDate today = LocalDate.now();
        totalFocusDays++;
        if (lastFocusDate == null || lastFocusDate.plusDays(1).equals(today)) {
            focusStreak++;
        } else if (!today.equals(lastFocusDate)) {
            focusStreak = 1;
        }
        lastFocusDate = today;

        addPoints(ProfileEvents.POINTS_PER_FOCUS_DAY);
        checkFocusAchievements();
        save();
    }

    public static void onFocusBroken() {
        ensureLoaded();
        focusStreak = 0;
        save();
    }

    // claim reward -> kurangi poin
    public static boolean spendPoints(int cost) {
        ensureLoaded();
        if (points < cost) return false;
        points -= cost;
        save();
        return true;
    }

    // ------------------------------------------------
    // Internal helpers
    // ------------------------------------------------
    private static void addPoints(int p) {
        points += p;
        xp += p * ProfileEvents.XP_PER_POINT;
    }

    private static void maybeResetWeekly() {
        // Reset weekly jika sudah lewat minggu (Senin awal minggu)
        // Simpel: jika hari ini == MONDAY & lastTaskCompletionDate != today & weeklyTasksCompleted > 0 & lastTaskCompletionDate < Monday?
        LocalDate today = LocalDate.now();
        if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
            // kalau last completion bukan hari ini -> reset
            if (lastTaskCompletionDate == null || !lastTaskCompletionDate.equals(today)) {
                weeklyTasksCompleted = 0;
                save();
            }
        }
    }

    private static void checkTaskAchievements() {
        if (totalTasksCompleted >= 1) {
            AchievementManager.unlock(ProfileEvents.ACH_TASK_STARTER);
        }
        if (taskStreak >= 7) {
            AchievementManager.unlock(ProfileEvents.ACH_TASK_STREAK7);
            // bonus poin sekali
            addPoints(ProfileEvents.BONUS_TASK_STREAK_7);
        }
    }

    private static void checkFocusAchievements() {
        if (totalFocusDays >= 1) {
            AchievementManager.unlock(ProfileEvents.ACH_FOCUS_DAY);
        }
        if (focusStreak >= 7) {
            AchievementManager.unlock(ProfileEvents.ACH_FOCUS_WEEK);
        }
    }

    public static void awardPoints(int amount) {
        if (amount <= 0) return;
        ensureLoaded();
        addPoints(amount);
        save();
    }
    public static void awardXPOnly(int amount) {
        if (amount <= 0) return;
        ensureLoaded();
        xp += amount;
        save();
    }

}
