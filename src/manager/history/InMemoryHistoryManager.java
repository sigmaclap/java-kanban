package manager.history;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LENGTH = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(history.size() < HISTORY_LENGTH){
            history.add(task);
        } else {
            history.remove(history.get(0));
            history.add(task);
        }

    }

    @Override
    public List<Task> getHistory() {
        for(int i = 0; i < history.size(); i++) {
            System.out.println(i + 1 +" - " + history.get(i));
        }
        return history;
    }
}
