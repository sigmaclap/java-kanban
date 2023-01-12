package manager.fileTaskManager;

import manager.historyTaskManager.HistoryManager;
import manager.ramTaskManager.InMemoryTaskManager;
import statusTasks.Status;
import statusTasks.TypeTasks;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private String LAST_LINE;

    public static void main(String[] args) {
        File file = new File("resources/doc.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        manager.createTask(new Task("Задача",
                "Описание", Status.IN_PROGRESS));
        manager.createTask(new Task("Задача 2",
                "Описание 2", Status.IN_PROGRESS));
        manager.createEpic(new Epic("Эпик 1",
                "Описание эпика 1", Status.NEW));
        manager.createSubTask(new Subtask("Сабтаск 1",
                "Описание сабтаска 1", Status.NEW, 3));
        manager.createSubTask(new Subtask("Сабтаск 2",
                "Описание сабтаска 2", Status.IN_PROGRESS, 3));
        manager.getTaskById(1);
        manager.getEpicTaskByID(3);
        manager.getTaskById(2);
        manager.getSubTaskByID(5);

        FileBackedTasksManager manager1 = loadFromFile(file);
        System.out.println(manager1.getAllTask());
        System.out.println(manager1.getAllEpicTask());
        System.out.println(manager1.getAllSubTask());
        System.out.println(manager1.getHistory());

    }

    public void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8), 1000)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(task + "\n");
            }
            for (Epic epic : getEpicTasks()) {
                fileWriter.write(epic + "\n");
            }
            for (Subtask subtask : getSubTasks()) {
                fileWriter.write(subtask + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(getHistoryManager()));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения данных.", exception);
        }
    }

    static String historyToString(HistoryManager historyManager) {
        List<String> var = new ArrayList<>();
        for (Task list : historyManager.getHistory()) {
            var.add(String.valueOf(list.getId()));
        }
        return String.join(",", var);
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        for (String line : manager.read()) {
            Task task = manager.fromString(line);
            manager.putTaskToManager(task);
        }
        manager.addToHistory(manager.LAST_LINE);
        return manager;
    }

    private void putTaskToManager(Task task) {
        if (task instanceof Subtask) {
            subTasks.put(task.getId(), (Subtask) task);
        } else if (task instanceof Epic) {
            epicTasks.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public List<String> read() {
        List<String> var = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8), 1000)) {
            fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                var.add(line);
            }
            LAST_LINE = fileReader.readLine();
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения данных.", exception);
        }
        return var;
    }

    public void addToHistory(String value) {
        for (Integer id : historyFromString(value)) {
            if (epicTasks.containsKey(id)) {
                historyManager.add(epicTasks.get(id));
            } else if (subTasks.containsKey(id)) {
                historyManager.add(subTasks.get(id));
            } else {
                historyManager.add(tasks.get(id));
            }
        }
    }

    public Task fromString(String value) {
        String[] var = value.split(",");
        TypeTasks type = TypeTasks.valueOf(var[1]);
        switch (type) {
            case TASK:
                return new Task(Integer.parseInt(var[0]), var[2], var[4], Status.valueOf(var[3]));
            case EPIC:
                return new Epic(Integer.parseInt(var[0]), var[2], var[4], Status.valueOf(var[3]));
            case SUBTASK:
                return new Subtask(Integer.parseInt(var[0]), var[2], var[4], Status.valueOf(var[3]), Integer.parseInt(var[5]));
            default:
                throw new IllegalArgumentException();
        }
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            List<String> id = List.of(value.split(","));
            for (String number : id) {
                history.add(Integer.valueOf(number));
            }
        } else {
            System.out.println("Пустая строка, либо неверено переданны данные.");
        }
        return history;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(Subtask subtask) {
        super.createSubTask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicTaskByID(int id) {
        Epic epic = super.getEpicTaskByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTaskByID(int id) {
        Subtask subtask = super.getSubTaskByID(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubs() {
        super.deleteAllSubs();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }
}
