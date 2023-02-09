package manager.httpTaskManager;

import manager.TaskManager;
import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private HttpTaskServer server;
    private final KVServer kvServer = new KVServer();


    public HttpTaskManagerTest() throws IOException {

    }

    @BeforeEach
    void setUp() throws IOException {
        kvServer.start();
        taskManager = new HttpTaskManager("http://localhost:8078");
        ManagerObject();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        kvServer.stop();
    }

    @Test
    void shouldLoadFromServer() {
        taskManager.getTaskById(1);
        taskManager.getSubTaskByID(5);
        TaskManager manager = new HttpTaskManager("http://localhost:8078");
        manager.loadFromServer();

        List<Task> expected = taskManager.getAllTasksByProject();
        List<Task> actual = manager.getAllTasksByProject();

        assertNotNull(manager.getAllTasksByProject());
        assertIterableEquals(expected, actual);
    }
}
