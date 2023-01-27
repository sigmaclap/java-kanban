package manager.historyTaskManager;

import manager.fileTaskManager.ManagerSaveException;
import manager.ramTaskManager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
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

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private InMemoryTaskManager taskManager;
    private static LocalDateTime START_TIME_TASK =
            LocalDateTime.of(2023, Month.JANUARY, 1, 13, 0);


    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
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
    void checkAddDuplicateTasks() {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);
        Task newTask1 = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK.plusMonths(1), 10);
        taskManager.createTask(newTask1);
        historyManager.add(newTask);
        historyManager.add(newTask1);
        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(newTask);
        historyManager.add(newTask);
        List<Task> expectedList = List.of(newTask1, taskManager.getTaskById(1), newTask);
        List<Task> actualList = historyManager.getHistory();

        assertNotNull(actualList);
        assertEquals(expectedList.toString(), actualList.toString());
        assertEquals(expectedList.size(), actualList.size());
    }

    @Test
    void removeTestHeadPosition() {
        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(taskManager.getEpicTaskByID(3));
        historyManager.add(taskManager.getSubTaskByID(4));
        historyManager.remove(taskManager.getTaskById(1).getId());
        List<Task> expectedList = List.of(
                taskManager.getEpicTaskByID(3),
                taskManager.getSubTaskByID(4)
        );
        List<Task> actualList = historyManager.getHistory();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    void removeTestMiddlePosition() {
        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(taskManager.getEpicTaskByID(3));
        historyManager.add(taskManager.getSubTaskByID(4));
        historyManager.remove(taskManager.getEpicTaskByID(3).getId());
        List<Task> expectedList = List.of(
                taskManager.getTaskById(1),
                taskManager.getSubTaskByID(4)
        );
        List<Task> actualList = historyManager.getHistory();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    void removeTestTailPosition() {
        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(taskManager.getEpicTaskByID(3));
        historyManager.add(taskManager.getSubTaskByID(4));
        historyManager.remove(taskManager.getSubTaskByID(4).getId());
        List<Task> expectedList = List.of(
                taskManager.getTaskById(1),
                taskManager.getEpicTaskByID(3)
        );
        List<Task> actualList = historyManager.getHistory();

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    void removeTestTailPositionWithEmptyData() {
        historyManager.add(taskManager.getEpicTaskByID(3));
        historyManager.add(taskManager.getSubTaskByID(4));
        Throwable ex = assertThrows(ManagerSaveException.class, () -> historyManager.remove(6));
        List<Task> expectedList = List.of(
                taskManager.getEpicTaskByID(3),
                taskManager.getSubTaskByID(4)
        );
        List<Task> actualList = historyManager.getHistory();

        assertNotNull(actualList);
        assertEquals("Невозможно удалить выбранную задачу из истории, укажите верный номер задачи",
                ex.getMessage());
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.toString(), actualList.toString());
    }

    @Test
    void checkAddEmptyHistory() {
        assertNotNull(historyManager.getHistory());
        assertIterableEquals(Collections.emptyList(), historyManager.getHistory());
    }
}