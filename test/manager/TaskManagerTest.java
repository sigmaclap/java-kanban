package manager;

import manager.fileTaskManager.ManagerSaveException;
import org.junit.jupiter.api.Test;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private static LocalDateTime START_TIME_TASK =
            LocalDateTime.of(2023, Month.JANUARY, 1, 13, 0);
    private static LocalDateTime START_TIME_SUBTASK =
            LocalDateTime.of(2023, Month.JANUARY, 2, 13, 0);

    public void ManagerObject() {
        taskManager.createTask(new Task("Задача",
                "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, Month.DECEMBER, 12, 12, 12), 5));
        taskManager.createTask(new Task("Задача 2",
                "Описание 2", Status.IN_PROGRESS,
                LocalDateTime.of(2022, Month.DECEMBER, 13, 12, 12), 5));
        taskManager.createEpic(new Epic("Эпик 1",
                "Описание эпика 1", Status.NEW, null, 0, null));
        taskManager.createSubTask(new Subtask("Сабтаск 1",
                "Описание сабтаска 1", Status.NEW,
                LocalDateTime.of(2022, Month.DECEMBER, 14, 12, 12), 5, 3));
        taskManager.createSubTask(new Subtask("Сабтаск 2",
                "Описание сабтаска 2", Status.IN_PROGRESS,
                LocalDateTime.of(2022, Month.DECEMBER, 15, 1, 12), 5, 3));
    }

    @Test
    void getAllTaskTest() {
        assertNotNull(taskManager.getAllTask());
        assertNotEquals(0, taskManager.getAllTask().size());
        assertEquals(2, taskManager.getAllTask().size());
    }

    @Test
    void getAllEpicTaskTest() {
        assertNotNull(taskManager.getAllEpicTask());
        assertEquals(1, taskManager.getAllEpicTask().size());
        assertNotEquals(0, taskManager.getAllEpicTask().size());
    }

    @Test
    void getAllSubTaskTest() {
        assertNotNull(taskManager.getAllSubTask());
        assertNotEquals(0, taskManager.getAllSubTask().size());
        assertEquals(2, taskManager.getAllSubTask().size());
    }

    @Test
    void deleteAllTaskTest() {
        taskManager.deleteAllTask();

        assertEquals(Collections.emptyList(), taskManager.getAllTask());
    }

    @Test
    void deleteAllEpicsTest() {
        taskManager.deleteAllEpics();

        assertEquals(Collections.emptyList(), taskManager.getAllEpicTask());

    }

    @Test
    void deleteAllSubsTest() {
        taskManager.deleteAllSubs();

        assertEquals(Collections.emptyList(), taskManager.getAllSubTask());

    }

    @Test
    void createTaskTest() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);

        assertNotNull(taskManager.getTaskById(newTask.getId()));
        assertEquals(newTask, taskManager.getTaskById(newTask.getId()));
    }

    @Test
    void createTaskWithIntersectionTest() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);
        Task newTask1 = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.createTask(newTask1));

        assertEquals("Невозможно создать задачу, задача пересекается по времени с уже существующей",
                ex.getMessage());
    }

    @Test
    void updateTaskWithIntersectionTest() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);
        Task newTask1 = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK.plusMonths(1), 10);
        taskManager.createTask(newTask1);
        newTask1.setStartTime(START_TIME_TASK.minusMonths(1));
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.updateTask(newTask1));

        assertEquals("Невозможно обновить задачу, задача пересекается по времени с уже существующей", ex.getMessage());
    }

    @Test
    void updateTaskTest() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);
        newTask.setDuration(50);
        newTask.setStartTime(newTask.getStartTime().plusMonths(3));
        newTask.setDescription("Des");
        taskManager.updateTask(newTask);

        assertEquals(50, newTask.getDuration());
        assertEquals("Des", newTask.getDescription());
        assertEquals(taskManager.getTaskById(newTask.getId()), newTask);
    }

    @Test
    void createEpicTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);

        assertNotNull(taskManager.getEpicTaskByID(newEpic.getId()));
        assertEquals(newEpic, taskManager.getEpicTaskByID(newEpic.getId()));
    }

    @Test
    void updateEpicTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        newEpic.setName("NewEpic");
        newEpic.setId(3);
        taskManager.updateEpic(newEpic);

        assertEquals(taskManager.getEpicTaskByID(3), newEpic);
        assertEquals("NewEpic", newEpic.getName());
    }

    @Test
    void createSubTaskTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        Subtask newSubtask1 = new Subtask("Subtask", "Description",
                Status.NEW, null, 60, newEpic.getId());
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.createSubTask(newSubtask1));


        assertNotNull(taskManager.getSubTaskByEpic(newEpic.getId()));
        assertEquals(newSubtask, taskManager.getSubTaskByID(newSubtask.getId()));
        assertEquals("Пустые дата и время при создании подзадачи", ex.getMessage());
    }

    @Test
    void updateSubTaskTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        newSubtask.setName("NewSubTask");
        newSubtask.setStartTime(START_TIME_TASK);
        taskManager.updateSubTask(newSubtask);

        assertNotNull(taskManager.getSubTaskByID(newSubtask.getId()));
        assertEquals("NewSubTask", newSubtask.getName());
        assertEquals(START_TIME_TASK, newSubtask.getStartTime());
    }

    @Test
    void updateSubTaskWithEmptyData() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        Subtask newSubtask1 = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK.plusHours(1), 60, newEpic.getId());
        taskManager.createSubTask(newSubtask1);
        newSubtask.setName("NewSubTask");
        newSubtask.setStartTime(START_TIME_TASK);
        taskManager.updateSubTask(newSubtask);
        taskManager.deleteSubTaskById(newSubtask.getId());

        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.updateSubTask(newSubtask));
        assertEquals("Подзадача не найдена.", ex.getMessage());
    }

    @Test
    void updateSubTaskWithIntersection() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        Subtask newSubtask1 = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());

        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.createSubTask(newSubtask1));
        assertEquals("Невозможно создать подзадачу, подзадача пересекается по времени с уже существующей",
                ex.getMessage());
    }

    @Test
    void getTaskByIdTest() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);

        assertThrows(ManagerSaveException.class, () -> taskManager.getTaskById(40));
        assertNotNull(taskManager.getTaskById(newTask.getId()));
        assertEquals(taskManager.getTaskById(newTask.getId()), newTask);
    }

    @Test
    void getEpicTaskByIDTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.getEpicTaskByID(40));

        assertEquals("Эпика по заданному ID не существует", ex.getMessage());
        assertNotNull(taskManager.getEpicTaskByID(newEpic.getId()));
        assertEquals(taskManager.getEpicTaskByID(newEpic.getId()), newEpic);

    }

    @Test
    void getSubTaskByIDTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.getSubTaskByID(40));

        assertEquals("Подзадачи по заданному ID не существует", ex.getMessage());
        assertNotNull(taskManager.getSubTaskByID(newSubtask.getId()));
        assertEquals(taskManager.getSubTaskByID(newSubtask.getId()), newSubtask);
    }

    @Test
    void checkCorrectTimeEpicWithTwoSubtasks() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        Subtask newSubtask1 = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK.plusHours(1), 60, newEpic.getId());
        taskManager.createSubTask(newSubtask1);

        LocalDateTime expectedStartTime = newSubtask.getStartTime();
        LocalDateTime expectedEndTime = newSubtask1.getEndTime();
        LocalDateTime actualStartTime = newEpic.getStartTime();
        LocalDateTime actualEndTime = newEpic.getEndTime();

        assertEquals(expectedStartTime, actualStartTime);
        assertEquals(expectedEndTime, actualEndTime);
    }

    @Test
    void deleteTaskByIdTest() {
        assertNotNull(taskManager.getTaskById(1));
        taskManager.deleteTaskById(1);
        Throwable ex = assertThrows(
                ManagerSaveException.class,
                () -> taskManager.getTaskById(1));
        String expectedMessageException = "Задачи по заданному ID не существует";

        assertEquals(expectedMessageException, ex.getMessage());
    }

    @Test
    void deleteTaskByIdTestWithEmptyDataTaskEpicSubTask() {
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.deleteTaskById(100));
        assertEquals("Задача не найдена, для удаления.", ex.getMessage());

        Throwable exEpic = assertThrows(ManagerSaveException.class, () -> taskManager.deleteEpicTaskById(100));
        assertEquals("Эпик не найден для удаления.", exEpic.getMessage());

        Throwable exSubTask = assertThrows(ManagerSaveException.class, () -> taskManager.deleteSubTaskById(100));
        assertEquals("Подзадача не найдена для удаления.", exSubTask.getMessage());

    }

    @Test
    void deleteEpicTaskByIdTest() {
        assertNotNull(taskManager.getEpicTaskByID(3));
        taskManager.deleteEpicTaskById(3);
        Throwable ex = assertThrows(
                ManagerSaveException.class,
                () -> taskManager.getEpicTaskByID(3));
        String expectedMessageException = "Эпика по заданному ID не существует";

        assertEquals(expectedMessageException, ex.getMessage());
    }

    @Test
    void deleteSubTaskByIdTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        int idSubtask = newSubtask.getId();
        assertNotNull(taskManager.getSubTaskByID(newSubtask.getId()));
        Throwable ex1 = assertThrows(ManagerSaveException.class, () -> taskManager.deleteSubTaskById(idSubtask));

        assertEquals("Невозможно обновить время эпика, без существующих подзадач", ex1.getMessage());
    }

    @Test
    void deleteSubTaskByIdTestToCheckPrioritizedList() {
        List<Task> expectedList = List.of(
                taskManager.getTaskById(1),
                taskManager.getTaskById(2),
                taskManager.getSubTaskByID(5)
        );
        taskManager.deleteSubTaskById(4);
        List<Task> actualList = taskManager.getPrioritizedTasks();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getSubTaskByEpicTest() {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, newEpic.getId());
        taskManager.createSubTask(newSubtask);
        List<Subtask> subtasks = taskManager.getSubTaskByEpic(newEpic.getId());

        assertEquals(1, taskManager.getSubTaskByEpic(newEpic.getId()).size());
        assertEquals(subtasks, taskManager.getSubTaskByEpic(newEpic.getId()));
    }

    @Test
    void getSubTaskByEpicWithEpicEmpty() {
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK, 60, 100);
        Throwable ex = assertThrows(ManagerSaveException.class, () -> taskManager.createSubTask(newSubtask));
        List<Subtask> subtasks = taskManager.getSubTaskByEpic(100);

        assertEquals("Невозможно создать подзадачу, не найден ID Эпика", ex.getMessage());
        assertEquals(subtasks, taskManager.getSubTaskByEpic(100));
    }

    @Test
    void getPrioritizedTasksTestValidData() {
        List<Task> expectedList = List.of(
                taskManager.getTaskById(1),
                taskManager.getTaskById(2),
                taskManager.getSubTaskByID(4),
                taskManager.getSubTaskByID(5)
        );
        List<Task> actualList = taskManager.getPrioritizedTasks();

        assertNotNull(actualList);
        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getPrioritizedTasksTestEmptyData() {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubs();
        taskManager.deleteAllEpics();
        List<Task> expectedList = Collections.emptyList();
        List<Task> actualList = taskManager.getPrioritizedTasks();

        assertEquals(0, actualList.size());
        assertEquals(Collections.emptyList(), actualList);
        assertIterableEquals(expectedList, actualList);
    }
}