package manager.httpTaskManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.fileTaskManager.FileBackedTasksManager;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super(null);
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
    }

    @Override
    public void save() {
        client.put("task", gson.toJson(getTasks()));
        client.put("epic", gson.toJson(getEpicTasks()));
        client.put("subtask", gson.toJson(getSubTasks()));
        client.put("history", gson.toJson(getHistory()));
    }

    @Override
    public void loadFromServer() {
        Type subTaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subTasksList = gson.fromJson(client.load("subtask"), subTaskType);
        addTasks(subTasksList);

        Type epicsType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(client.load("epic"), epicsType);
        addTasks(epicsList);

        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(client.load("task"), tasksType);
        addTasks(tasksList);

        Type historyType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> historyList = gson.fromJson(client.load("history"), historyType);
        addHistory(historyList);
    }

    protected void addTasks(List<? extends Task> tasksList) {
        for (Task task : tasksList) {
            switch (task.getTypeTasks()) {
                case TASK:
                    tasks.put(task.getId(), task);
                    prioritiesStorageTasks.add(task);
                    break;
                case EPIC:
                    epicTasks.put(task.getId(), (Epic) task);
                    break;
                case SUBTASK:
                    subTasks.put(task.getId(), (Subtask) task);
                    prioritiesStorageTasks.add(task);
                    break;
            }
        }
    }

    protected void addHistory(List<Task> historyList) {
        for (Task task : historyList) {
            int id = task.getId();
            if (epicTasks.containsKey(id)) {
                historyManager.add(epicTasks.get(id));
            } else if (subTasks.containsKey(id)) {
                historyManager.add(subTasks.get(id));
            } else {
                historyManager.add(tasks.get(id));
            }
        }
    }
}
