package manager.ramTaskManager;

import manager.Managers;
import manager.TaskManager;
import manager.historyTaskManager.HistoryManager;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    int idTaskManager = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Task> getAllTask() {
        if (tasks.isEmpty()) {
            System.out.println("Задач не найдено");
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicTask() {
        if (epicTasks.isEmpty()) {
            System.out.println("Эпиков не найдено");
        }
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTask() {
        if (subTasks.isEmpty()) {
            System.out.println("Подзадач не найдено");
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTask() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() {
        for (int id0 : subTasks.keySet()) {
            historyManager.remove(id0);
        }
        for (int id : epicTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        epicTasks.clear();

    }

    @Override
    public void deleteAllSubs() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtaskIds().clear();
            updateStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicTaskByID(int id) {
        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public Subtask getSubTaskByID(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        int id = ++idTaskManager;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int id = ++idTaskManager;
        epic.setId(id);
        epicTasks.put(id, epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            epicTasks.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    @Override
    public void createSubTask(Subtask subtask) {
        int id = ++idTaskManager;
        subtask.setId(id);
        Epic epic = epicTasks.get(subtask.getEpicId());
        if (epic != null) {
            subTasks.put(id, subtask);
            epic.addSubtaskIds(id);
            updateStatus(epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            Epic epic = epicTasks.get(subtask.getEpicId());
            if (epicTasks.containsValue(epic)) {
                subTasks.put(subtask.getId(), subtask);
                updateStatus(epic);
            } else {
                System.out.println("Эпик не найден, обновление подзадачи невозможно.");
            }
        } else {
            System.out.println("Подзадача не найдена.");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача не найдена, для удаления.");
        }
    }

    @Override
    public void deleteEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        if (epicTasks.containsKey(id)) {
            for (Integer subTaskId : epic.getSubtaskIds()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик не найден для удаления.");
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        Subtask subtask = subTasks.get(id);
        if (subTasks.containsKey(id)) {
            Epic epic = epicTasks.get(subtask.getEpicId());
            Integer subtaskId = subtask.getId();
            epic.getSubtaskIds().remove(subtaskId);
            subTasks.remove(id);
            historyManager.remove(id);
            updateStatus(epic);
        } else {
            System.out.println("Подзадача не найдена для удаления.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubTaskByEpic(int id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Subtask> newSubTask = new ArrayList<>();
            Epic epic = epicTasks.get(id);
            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                newSubTask.add(subTasks.get(epic.getSubtaskIds().get(i)));
            }
            return newSubTask;

        } else {
            return new ArrayList<>();
        }
    }

    public void updateStatus(Epic epic) {
        if (epic == null) {
            return;
        }

        ArrayList<Subtask> subtasks = getSubTaskByEpic(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Set<Status> statusSet = new HashSet<>();
        for (Subtask subtask : subtasks) {
            Status taskStatus = subtask.getStatus();
            statusSet.add(taskStatus);
        }
        if (statusSet.size() > 1) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(statusSet.iterator().next());
        }
    }
}