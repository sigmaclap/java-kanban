package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statusTasks.Status;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic;
    private final TaskManager manager = Managers.getDefault();

    private static LocalDateTime START_TIME_SUBTASK_1 =
            LocalDateTime.of(2023, Month.JANUARY, 1, 13, 0);
    private static LocalDateTime START_TIME_SUBTASK_2 =
            LocalDateTime.of(2023, Month.JANUARY, 1, 14, 0);

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        manager.createEpic(epic);

    }

    @Test
    void shouldShowSubTaskListIsEmpty() {
        Status expectedStatus = Status.NEW;
        Status actualStatus = epic.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldShowAllSubTasksWithStatusNew() {
        manager.createSubTask(new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK_1, 60, 1));
        manager.createSubTask(new Subtask("Subtask1", "Description2",
                Status.NEW, START_TIME_SUBTASK_2, 180, 1));

        Status expectedStatus = Status.NEW;
        Status actualStatus = epic.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldShowAllSubTasksWithStatusDone() {
        manager.createSubTask(new Subtask("Subtask", "Description",
                Status.DONE, START_TIME_SUBTASK_1, 60, 1));
        manager.createSubTask(new Subtask("Subtask1", "Description2",
                Status.DONE, START_TIME_SUBTASK_2, 180, 1));

        Status expectedStatus = Status.DONE;
        Status actualStatus = epic.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldShowAllSubTasksWithStatusNewAndDone() {
        manager.createSubTask(new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK_1, 60, 1));
        manager.createSubTask(new Subtask("Subtask1", "Description2",
                Status.DONE, START_TIME_SUBTASK_2, 180, 1));

        Status expectedStatus = Status.IN_PROGRESS;
        Status actualStatus = epic.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldShowAllSubTasksWithStatusInProgress() {
        manager.createSubTask(new Subtask("Subtask", "Description",
                Status.IN_PROGRESS, START_TIME_SUBTASK_1, 60, 1));
        manager.createSubTask(new Subtask("Subtask1", "Description2",
                Status.IN_PROGRESS, START_TIME_SUBTASK_2, 180, 1));

        Status expectedStatus = Status.IN_PROGRESS;
        Status actualStatus = epic.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void shouldShowReturnDuration240ForTwoSubtask() {
        Subtask subtask1 = new Subtask("Subtask", "Description",
                Status.IN_PROGRESS, START_TIME_SUBTASK_1, 60, 1);
        Subtask subtask2 = new Subtask("Subtask1", "Description2",
                Status.IN_PROGRESS, START_TIME_SUBTASK_2, 180, 1);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        long expectedDuration = epic.getDuration();
        long actualDuration = subtask1.getDuration() + subtask2.getDuration();

        assertEquals(expectedDuration, actualDuration);
    }
}