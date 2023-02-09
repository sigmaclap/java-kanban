package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private final Gson gson = Managers.getGson();
    private TaskManager taskManager;
    private final KVServer kvServer = new KVServer();
    private static final LocalDateTime START_TIME_TASK =
            LocalDateTime.of(2023, Month.JANUARY, 1, 13, 0);
    private static final LocalDateTime START_TIME_SUBTASK =
            LocalDateTime.of(2023, Month.JANUARY, 2, 13, 0);

    HttpTaskServerTest() throws IOException {
    }


    @BeforeEach
    void setUp() throws IOException {
        kvServer.start();
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
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
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        kvServer.stop();
    }

    @Test
    void getAllTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type typeTask = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), typeTask);
        List<Task> expected = taskManager.getAllTask();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertIterableEquals(expected, actual);
    }


    @Test
    void testIncorrectRequestPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/asd");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("This path does not exist", response.body());
    }

    @Test
    void testIncorrectRequestDeleteHistoryPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("Incorrect request method", response.body());
    }

    @Test
    void testIncorrectRequestDeleteAllTasksPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("Incorrect request method", response.body());
    }

    @Test
    void getTaskByIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(taskManager.getTaskById(1));

        Task task = taskManager.getTaskById(1);
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actualTask = gson.fromJson(response.body(), taskType);

        assertEquals(task, actualTask);
    }

    @Test
    void getTaskByInvalidIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Empty Value is ID - 100", response.body());
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task deleted ID - 1", response.body());
    }

    @Test
    void deleteAllTasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("All tasks have been successfully deleted!", response.body());
    }

    @Test
    void postTaskTest() throws IOException, InterruptedException {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        String jsonTask = gson.toJson(newTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully created, ID = 6", response.body());

        URI url1 = URI.create("http://localhost:8080/tasks/task/?id=6");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Type typeNewTask = new TypeToken<Task>() {
        }.getType();
        Task actualTask = gson.fromJson(response1.body(), typeNewTask);
        Task expectedTask = taskManager.getTaskById(actualTask.getId());


        assertEquals(200, response1.statusCode());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void postUpdateTaskTest() throws IOException, InterruptedException {
        Task newTask = new Task("Task", "Description",
                Status.NEW, START_TIME_TASK, 10);
        taskManager.createTask(newTask);
        newTask.setName("NEW_TASK");
        newTask.setDescription("NEW_DESCRIPTION");

        String jsonTask = gson.toJson(newTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully updated, ID = 6", response.body());
    }

    @Test
    void getAllEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type typeTask = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), typeTask);
        List<Epic> expected = taskManager.getAllEpicTask();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void getSubTaskByEpicIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type typeTask = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), typeTask);
        List<Subtask> expected = taskManager.getAllSubTask();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void getEpicByIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(taskManager.getEpicTaskByID(3));

        Epic epic = taskManager.getEpicTaskByID(3);
        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actualTask = gson.fromJson(response.body(), taskType);

        assertEquals(epic, actualTask);
    }

    @Test
    void getEpicByInvalidIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Empty Value is ID - 100", response.body());
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task deleted ID - 3", response.body());
    }

    @Test
    void deleteAllEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("All epics have been successfully removed!", response.body());
    }

    @Test
    void postEpicTest() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        String jsonTask = gson.toJson(newEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully created, ID = 6", response.body());

        URI url1 = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<Epic>() {
        }.getType();
        Epic actualTask = gson.fromJson(response1.body(), type);
        Epic expectedTask = taskManager.getEpicTaskByID(actualTask.getId());


        assertEquals(200, response1.statusCode());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void postUpdateEpicTest() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic", "Description",
                Status.NEW, null, 0, null);
        taskManager.createEpic(newEpic);
        newEpic.setName("NEW_TASK");
        newEpic.setDescription("NEW_DESCRIPTION");

        String jsonTask = gson.toJson(newEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully updated, ID = 6", response.body());
    }

    @Test
    void getSubTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type typeTask = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), typeTask);
        List<Subtask> expected = taskManager.getAllSubTask();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void getSubTaskByIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(taskManager.getSubTaskByID(4));

        Subtask subtask = taskManager.getSubTaskByID(4);
        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actualTask = gson.fromJson(response.body(), taskType);

        assertEquals(subtask, actualTask);
    }

    @Test
    void getSubTaskByInvalidIDTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Empty Value is ID - 100", response.body());
    }

    @Test
    void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Subtask removed ID - 4", response.body());
    }

    @Test
    void deleteAllSubTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("All subtasks have been successfully deleted!", response.body());
    }

    @Test
    void postSubTaskTest() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK.plusMonths(5), 60, 3);
        String jsonTask = gson.toJson(newSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully created, ID = 6", response.body());

        URI url1 = URI.create("http://localhost:8080/tasks/subtask/?id=6");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<Subtask>() {
        }.getType();
        Subtask actualTask = gson.fromJson(response1.body(), type);
        Subtask expectedTask = taskManager.getSubTaskByID(actualTask.getId());


        assertEquals(200, response1.statusCode());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void postUpdateSubTaskTest() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("Subtask", "Description",
                Status.NEW, START_TIME_SUBTASK.plusMonths(5), 60, 3);
        taskManager.createSubTask(newSubtask);
        newSubtask.setName("NEW_TASK");
        newSubtask.setDescription("NEW_DESCRIPTION");

        String jsonTask = gson.toJson(newSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Task successfully updated, ID = 6", response.body());
    }

    @Test
    void getAllTasksByProject() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), type);

        assertNotNull(actual);
        assertEquals(5, actual.size());
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> responseJson = gson.fromJson(response.body(), type);
        List<Task> tasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        List<Task> tasksAllList = new ArrayList<>();
        for (Task task : responseJson) {
            if (taskManager.getMapSubTasks().containsKey(task.getId())) {
                subtasks.add((Subtask) task);
            } else if (taskManager.getMapEpics().containsKey(task.getId())) {
                epics.add((Epic) task);
            } else {
                tasks.add(task);
            }
        }
        tasksAllList.addAll(tasks);
        tasksAllList.addAll(epics);
        tasksAllList.addAll(subtasks);

        List<Task> expected = taskManager.getHistory();

        assertNotNull(tasksAllList);
        assertEquals(2, tasksAllList.size());
        assertIterableEquals(expected, tasksAllList);
    }
}