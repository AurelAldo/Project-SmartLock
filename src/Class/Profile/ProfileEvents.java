package Class.Profile;

public final class ProfileEvents {
    private ProfileEvents() {}

    // Poin
    public static final int POINTS_PER_TASK = 10;
    public static final int POINTS_PER_FOCUS_DAY = 5;
    public static final int BONUS_TASK_STREAK_7 = 30;

    // XP
    public static final int XP_PER_POINT = 1;  // XP = poin (simple)

    // Level
    public static int xpNeededForLevel(int level) {
        return level * 100; // linear
    }

    // Achievement Names (harus sama dengan label di Achievement.xml & tampilan)
    public static final String ACH_TASK_STARTER = "Task Starter";   // 1 task
    public static final String ACH_TASK_STREAK7 = "Task Streak";    // 7 berturut
    public static final String ACH_FOCUS_DAY    = "Focus Day";      // 1 fokus
    public static final String ACH_FOCUS_WEEK   = "Focus Week";     // 7 fokus
}
