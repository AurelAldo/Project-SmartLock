package Class.TaskControl;

public class TaskTrackerHolder {
    private static TaskTrackController instance;
    public static void setInstance(TaskTrackController c) { instance = c; }
    public static TaskTrackController getInstance() { return instance; }
}
