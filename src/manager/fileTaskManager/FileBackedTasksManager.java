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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private static String LAST_LINE;


    public void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8), 1000)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
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
        List<String> historyList = new ArrayList<>();
        for (Task list : historyManager.getHistory()) {
            historyList.add(String.valueOf(list.getId()));
        }
        return String.join(",", historyList);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        for (String line : manager.read()) {
            Task task = manager.fromString(line);
            manager.putTaskToManager(task);
        }
        manager.addToHistory(LAST_LINE);
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
        List<String> bufferedLines = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8), 1000)) {
            fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                bufferedLines.add(line);
            }
            LAST_LINE = fileReader.readLine();
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения данных.", exception);
        }
        return bufferedLines;
    }

    public void addToHistory(String value) {
        if (value == null) {
            return;
        }
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
        String[] values = value.split(",");
        TypeTasks type = TypeTasks.valueOf(values[1]);
        try {
            LocalDateTime startTime = LocalDateTime.parse(values[5]);
            switch (type) {
                case TASK:
                    return new Task(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]), startTime, Long.parseLong(values[6]));
                case EPIC:
                    return new Epic(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]), startTime, Long.parseLong(values[6]), LocalDateTime.parse(values[7]));
                case SUBTASK:
                    return new Subtask(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]), startTime, Long.parseLong(values[6]), Integer.parseInt(values[8]));
                default:
                    throw new IllegalArgumentException();
            }
        } catch (DateTimeParseException exception) {
            throw new TaskValidationException("Невозможно рассчитать начало задачи для эпика, подзадачи отсутствуют");
        }

    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            List<String> id = List.of(value.split(","));
            for (String number : id) {
                history.add(Integer.valueOf(number));
            }
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
