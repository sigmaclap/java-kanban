package manager;

import manager.historyTaskManager.HistoryManager;
import manager.historyTaskManager.InMemoryHistoryManager;
import manager.ramTaskManager.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
