package Manager;

import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;
import java.util.*;

public class ManagerTasks {
    int idTaskManager = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();

    public ArrayList<Task> getAllTask(){ // Получение списка всех задач
        if (tasks.isEmpty()) {
            System.out.println("Задач не найдено");
        }
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpicTask(){
        if (epicTasks.isEmpty()) {
            System.out.println("Эпиков не найдено");
        }
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Subtask> getAllSubTask(){
        if (subTasks.isEmpty()) {
            System.out.println("Подзадач не найдено");
        }
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTask(){
        tasks.clear();
    }

    public void deleteAllEpics(){
        epicTasks.clear();
    }

    public void deleteAllSubs(){
        subTasks.clear();
    }


    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicTaskByID(int id){
        return epicTasks.get(id);
    }

    public Subtask getSubTaskByID(int id){
        return subTasks.get(id);
    }

    public void createTask(Task task) {
        int id = ++idTaskManager;
        task.setIdTask(id);
        tasks.put(id, task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getIdTask())){
            tasks.put(task.getIdTask(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    public void createEpic(Epic epic) {
        int id = ++idTaskManager;
        epic.setIdTask(id);
        epicTasks.put(id, epic);
    }

    public void updateEpic(Epic epic) {
        if (epicTasks.containsKey(epic.getIdTask())){
            epicTasks.put(epic.getIdTask(), epic);
            updateStatus(epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    public void createSubTask(Subtask subtask) {
        int id = ++idTaskManager;
        subtask.setIdTask(id);
        Epic epic = epicTasks.get(subtask.getEpicId());
        if (epic != null) {
            subTasks.put(id, subtask);
            epic.setSubTasksId(id);
            updateStatus(epic);
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    public void updateSubTask(Subtask subtask) {
        if (subTasks.containsKey(subtask.getIdTask())){
            Epic epic = epicTasks.get(subtask.getEpicId());
            subTasks.put(subtask.getIdTask(), subtask);
            updateStatus(epic);
        } else {
            System.out.println("Подзадача не найдена.");
        }
    }

    public void deleteTaskById(Integer id) {
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else {
            System.out.println("Задача не найдена, для удаления.");
        }
    }

    public void deleteEpicTaskById (int id){
        if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
        } else {
            System.out.println("Эпик не найден для удаления.");
        }
    }

    public void deleteSubTaskById (int id){
        if (subTasks.containsKey(id)) {
            subTasks.remove(id);
        } else {
            System.out.println("Подзадача не найдена для удаления.");
        }
    }

    public ArrayList<Subtask> printSubTaskByEpic (int id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Subtask> newSubTask = new ArrayList<>();
            Epic epic = epicTasks.get(id);
            for (int i = 0; i < epic.getSubTasksId().size(); i++) {
                newSubTask.add(subTasks.get(epic.getSubTasksId().get(i)));
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

        ArrayList<Subtask> subtasks = printSubTaskByEpic(epic.getIdTask());
        if (subtasks.size() == 0) {
            epic.setStatusTask("NEW");
        }

        Set<String> statusSet = new HashSet<>();
        for (Subtask subtask : subtasks) {
            String taskStatus = subtask.getStatusTask();
            statusSet.add(taskStatus);
        }
        if (statusSet.size() > 1) {
            epic.setStatusTask("IN_PROGRESS");
        } else if (statusSet.size() == 1){
            epic.setStatusTask(statusSet.iterator().next());
        }
    }

    public String statusTasks(Integer value) {
        String[] status = {"NEW", "IN_PROGRESS", "DONE"};
        return status[value];
    }
}