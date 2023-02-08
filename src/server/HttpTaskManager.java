package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.fileTaskManager.FileBackedTasksManager;
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
        if (getTasks().isEmpty()) {
            return;
        } else {
            client.put("task", gson.toJson(getTasks()));
        }
        if (getEpicTasks().isEmpty()) {
            return;
        } else {
            client.put("epic", gson.toJson(getEpicTasks()));
        }
        if (getSubTasks().isEmpty()) {
            return;
        } else {
            client.put("subtask", gson.toJson(getSubTasks()));
        }
        if (!getHistory().isEmpty()) {
            client.put("history", gson.toJson(getHistory()));
        }
    }

    @Override
    public void loadFromServer() {
        Type subTaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subTasksList = gson.fromJson(client.load("subtask"), subTaskType);
        for (Subtask subtask : subTasksList) {
            subTasks.put(subtask.getId(), subtask);
            prioritiesStorageTasks.add(subtask);
        }
        Type epicsType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(client.load("epic"), epicsType);
        for (Epic epic : epicsList) {
            epicTasks.put(epic.getId(), epic);
        }
        Type tasksType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(client.load("task"), tasksType);
        for (Task task : tasksList) {
            tasks.put(task.getId(), task);
            prioritiesStorageTasks.add(task);
        }
        Type historyType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> historyList = gson.fromJson(client.load("history"), historyType);
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
