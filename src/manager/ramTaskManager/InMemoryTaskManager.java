package manager.ramTaskManager;

import manager.Managers;
import manager.TaskManager;
import manager.fileTaskManager.TaskValidationException;
import manager.historyTaskManager.HistoryManager;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    int idTaskManager = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epicTasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subTasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    Comparator<Task> comparatorTasks = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected Set<Task> prioritiesStorageTasks = new TreeSet<>(comparatorTasks);


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

    public Set<Task> getPrioritiesStorageTasks() {
        return prioritiesStorageTasks;
    }

    public void setPrioritiesStorageTasks(Set<Task> prioritiesStorageTasks) {
        this.prioritiesStorageTasks = prioritiesStorageTasks;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicTask() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTask() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        prioritiesStorageTasks.clear();
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int idSub : subTasks.keySet()) {
            historyManager.remove(idSub);
        }
        for (int idEpic : epicTasks.keySet()) {
            historyManager.remove(idEpic);
        }
        subTasks.clear();
        epicTasks.clear();

    }

    @Override
    public void deleteAllSubs() {
        if (!historyManager.getHistory().isEmpty()) {
            for (int id : subTasks.keySet()) {
                historyManager.remove(id);
            }
        }
        subTasks.clear();
        prioritiesStorageTasks.removeIf(Subtask.class::isInstance);
        for (Epic epic : epicTasks.values()) {
            epic.getSubtaskIds().clear();
            updateStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        try {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } catch (NullPointerException exception) {
            throw new TaskValidationException("Задачи по заданному ID не существует");
        }
    }

    @Override
    public Epic getEpicTaskByID(int id) {
        try {
            historyManager.add(epicTasks.get(id));
            return epicTasks.get(id);
        } catch (NullPointerException exception) {
            throw new TaskValidationException("Эпика по заданному ID не существует");
        }
    }

    @Override
    public Subtask getSubTaskByID(int id) {
        try {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } catch (NullPointerException exception) {
            throw new TaskValidationException("Подзадачи по заданному ID не существует");
        }
    }

    @Override
    public void createTask(Task task) {
        int id = ++idTaskManager;
        task.setId(id);
        if (isNoTasksIntersections(task)) {
            tasks.put(id, task);
            prioritiesStorageTasks.add(task);
        } else {
            throw new TaskValidationException("Невозможно создать задачу, задача пересекается по времени с уже существующей");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            task.setId(task.getId());
            if (prioritiesStorageTasks.contains(tasks.get(task.getId()))) {
                prioritiesStorageTasks.add(task);
                return;
            }
            if (isNoTasksIntersections(task)) {
                prioritiesStorageTasks.add(task);
            } else {
                throw new TaskValidationException("Невозможно обновить задачу, задача пересекается по времени с уже существующей");
            }
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
            updateStatus(epicTasks.get(epic.getId()));
            updateStatus(epic);
            updateTimeEpic(epic);
        } else {
            throw new TaskValidationException("Эпик не найден.");
        }
    }

    @Override
    public void createSubTask(Subtask subtask) {
        int id = ++idTaskManager;
        subtask.setId(id);
        Epic epic = epicTasks.get(subtask.getEpicId());
        if (epic != null) {
            if (isNoTasksIntersections(subtask)) {
                prioritiesStorageTasks.add(subtask);
                subTasks.put(id, subtask);
                epic.addSubtaskIds(id);
                updateStatus(epic);
                if (!(subtask.getStartTime() == null)) {
                    updateTimeEpic(epic);
                }
            } else {
                throw new TaskValidationException("Невозможно создать подзадачу, подзадача пересекается по времени с уже существующей");
            }
        }
        if (epic == null) {
            throw new TaskValidationException("Невозможно создать подзадачу, не найден ID Эпика");
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            Epic epic = epicTasks.get(subtask.getEpicId());
            if (epicTasks.containsValue(epic)) {
                subTasks.put(subtask.getId(), subtask);
                updateStatus(epic);
                updateTimeEpic(epic);
                if (prioritiesStorageTasks.contains(subTasks.get(subtask.getId()))) {
                    prioritiesStorageTasks.add(subtask);
                    return;
                }
                if (isNoTasksIntersections(subtask)) {
                    prioritiesStorageTasks.add(subtask);
                }
            }
        } else {
            throw new TaskValidationException("Подзадача не найдена.");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            prioritiesStorageTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);
        } else {
            throw new TaskValidationException("Задача не найдена, для удаления.");
        }
    }

    @Override
    public void deleteEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        if (epicTasks.containsKey(id)) {
            for (Integer subTaskId : epic.getSubtaskIds()) {
                subTasks.remove(subTaskId);
                prioritiesStorageTasks.removeIf(Subtask.class::isInstance);
                prioritiesStorageTasks.remove(epic);
                if (historyManager.getHistory().contains(subTaskId)) {
                    historyManager.remove(subTaskId);
                }
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new TaskValidationException("Эпик не найден для удаления.");
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
            if (historyManager.getHistory().contains(id)) {
                historyManager.remove(id);
            }
            updateStatus(epic);
            updateTimeEpic(epic);
            prioritiesStorageTasks.remove(subtask);
        } else {
            throw new TaskValidationException("Подзадача не найдена для удаления.");
        }
    }

    @Override
    public List<Subtask> getSubTaskByEpic(int id) {
        if (epicTasks.containsKey(id)) {
            List<Subtask> newSubTask = new ArrayList<>();
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

        List<Subtask> subtasks = getSubTaskByEpic(epic.getId());
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritiesStorageTasks);
    }

    @Override
    public void updateTimeEpic(Epic epic) {
        List<Subtask> subtasks = getSubTaskByEpic(epic.getId());
        if (subtasks.isEmpty()) {
            return;
        }
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(Duration.between(startTime, endTime).toMinutes());
    }

    public boolean isNoTasksIntersections(Task task) {
        if (task == null) {
            return false;
        }
        if (task.getStartTime() == null) {
            return true;
        }
        if (!prioritiesStorageTasks.isEmpty()) {
            for (Task taskStorage : prioritiesStorageTasks) {
                if (taskStorage.getStartTime() == null) {
                    continue;
                }
                if ((task.getStartTime().isAfter(taskStorage.getStartTime())
                        || task.getStartTime().equals(taskStorage.getStartTime()))
                        && (task.getEndTime().isBefore(taskStorage.getEndTime()))
                        || task.getEndTime().equals(taskStorage.getEndTime())) {
                    return false;
                }
            }
        }
        return true;
    }
}