package manager.fileTaskManager;

import manager.TaskManagerTest;
import org.junit.jupiter.api.Test;
import statusTasks.Status;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    public FileBackedTasksManagerTest() {
        file = new File("resources/doctest.csv");
        taskManager = new FileBackedTasksManager(file);
        ManagerObject();
    }

    @Test
    void testErrorWriterFile() {
        File file1 = new File("resources/test.csv");
        boolean delete = file1.delete();
        Throwable ex = assertThrows(ManagerSaveException.class, () -> FileBackedTasksManager.loadFromFile(file1));

        assertFalse(delete);
        assertEquals("Ошибка чтения данных.", ex.getMessage());
    }

    @Test
    void testErrorSaveFile() {
        File file1 = new File("ASDQWE/test.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(file1);
        Throwable ex = assertThrows(ManagerSaveException.class, () -> manager.createTask(new Task("Задача",
                "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, Month.DECEMBER, 12, 12, 12), 5)));

        assertEquals("Ошибка сохранения данных.", ex.getMessage());
    }

    @Test
    void checkSuccessSaveAndLoadFromFile() {
        List<Task> listTask = taskManager.getAllTask();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(2, manager.getAllTask().size());
        assertEquals(1, manager.getAllEpicTask().size());
        assertEquals(2, manager.getAllSubTask().size());
        assertIterableEquals(listTask, manager.getAllTask());
    }

    @Test
    void checkSuccessSaveAndLoadHistoryFromFile() {
        taskManager.getTaskById(1);
        taskManager.getSubTaskByID(4);
        taskManager.getEpicTaskByID(3);
        taskManager.getTaskById(2);
        taskManager.getSubTaskByID(5);
        List<Task> expectedHistory = taskManager.getHistory();
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        List<Task> actualHistory = manager.getHistory();

        assertNotNull(actualHistory);
        assertEquals(expectedHistory.size(), actualHistory.size());
        assertEquals(expectedHistory.get(0).toString(), actualHistory.get(0).toString());
        assertIterableEquals(Collections.singleton(expectedHistory.toString()),
                Collections.singleton(actualHistory.toString()));
    }

    @Test
    void checkHistoryWithEmptyLineInDocument() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        List<Task> actualHistory = manager.getHistory();

        assertEquals(Collections.emptyList(), actualHistory);
    }

    @Test
    void checkEpicWithNoSubtasks() {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubs();
        taskManager.deleteAllEpics();
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Throwable ex = assertThrows(TaskValidationException.class, () -> FileBackedTasksManager.loadFromFile(file));

        assertEquals("Невозможно рассчитать начало задачи для эпика, подзадачи отсутствуют", ex.getMessage());
    }
}