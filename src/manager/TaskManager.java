package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    ArrayList<Task> getAllTask();

    ArrayList<Epic> getAllEpicTask();

    ArrayList<Subtask> getAllSubTask();


    void deleteAllTask();

    void deleteAllEpics();

    void deleteAllSubs();

    Task getTaskById(int id);

    Epic getEpicTaskByID(int id);

    Subtask getSubTaskByID(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void createSubTask(Subtask subtask);

    void updateSubTask(Subtask subtask);

    void deleteTaskById(Integer id);

    void deleteEpicTaskById(int id);

    void deleteSubTaskById(int id);

    List<Subtask> getSubTaskByEpic(int id);

    List<Task> getPrioritizedTasks();

    void updateTimeEpic(Epic epic);
}
