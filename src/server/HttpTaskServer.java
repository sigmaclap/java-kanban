package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TasksHandler());

    }


    public class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String[] endPoint = exchange.getRequestURI().getPath().split("/");
                if (Pattern.matches("^/tasks/$", path)) {
                    allTasksHandler(exchange);
                    return;
                }
                switch (endPoint[2]) {
                    case "task":
                        taskHandler(exchange);
                        break;
                    case "epic":
                        epicHandler(exchange);
                        break;
                    case "subtask":
                        subTaskHandler(exchange);
                        break;
                    case "history":
                        historyHandler(exchange);
                        break;
                    default:
                        writeResponse(exchange, "This path does not exist", 400);
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            } finally {
                exchange.close();
            }

        }

        private void taskHandler(HttpExchange exchange) {
            try {
                String path = String.valueOf(exchange.getRequestURI());
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(path), String.valueOf(StandardCharsets.UTF_8));
                String requestMethod = exchange.getRequestMethod();
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                switch (requestMethod) {
                    case "GET":
                        if (params.isEmpty()) {
                            String response = gson.toJson(taskManager.getAllTask());
                            sendText(exchange, response);
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (param.getName().equals("id") && taskManager.getMapTasks().containsKey(id)) {
                                String response = gson.toJson(taskManager.getTaskById(id));
                                sendText(exchange, response);
                            } else {
                                writeResponse(exchange, "Empty Value is ID - " + id, 400);
                            }
                        }
                        break;
                    case "POST":
                        if (isJsonValid(body)) {
                            Task task = gson.fromJson(body, Task.class);
                            Integer id = task.getId();
                            if (id == null) {
                                taskManager.createTask(task);
                                sendText(exchange, "Task successfully created, ID = " + task.getId());
                            } else {
                                taskManager.updateTask(task);
                                sendText(exchange, "Task successfully updated, ID = " + task.getId());
                            }
                        } else {
                            writeResponse(exchange, "Incorrect JSON received", 400);
                        }
                        break;
                    case "DELETE":
                        if (params.isEmpty()) {
                            taskManager.deleteAllTask();
                            sendText(exchange, "All tasks have been successfully deleted!");
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (param.getName().equals("id") && taskManager.getMapTasks().containsKey(id)) {
                                taskManager.deleteTaskById(id);
                                sendText(exchange, "Task deleted ID - " + id);
                                return;
                            } else {
                                writeResponse(exchange,
                                        "Uninstall error, ID not found - " + id, 400);
                            }
                        }
                        break;
                    default:
                        writeResponse(exchange, "Incorrect request method", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void epicHandler(HttpExchange exchange) {
            try {
                String path = String.valueOf(exchange.getRequestURI());
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(path), String.valueOf(StandardCharsets.UTF_8));
                String requestMethod = exchange.getRequestMethod();
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                switch (requestMethod) {
                    case "GET":
                        if (params.isEmpty()) {
                            String response = gson.toJson(taskManager.getAllEpicTask());
                            sendText(exchange, response);
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (param.getName().equals("id") && taskManager.getMapEpics().containsKey(id)) {
                                String response = gson.toJson(taskManager.getEpicTaskByID(id));
                                sendText(exchange, response);
                            } else {
                                writeResponse(exchange, "Empty Value is ID - " + id, 400);
                            }
                        }
                        break;
                    case "POST":
                        if (isJsonValid(body)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            Integer id = epic.getId();
                            if (id == null) {
                                taskManager.createEpic(epic);
                                sendText(exchange, "Task successfully created, ID = " + epic.getId());
                            } else {
                                taskManager.updateEpic(epic);
                                sendText(exchange, "Task successfully updated, ID = " + epic.getId());
                            }
                        } else {
                            writeResponse(exchange, "Incorrect JSON received", 400);
                        }
                        break;
                    case "DELETE":
                        if (params.isEmpty()) {
                            taskManager.deleteAllEpics();
                            sendText(exchange, "All epics have been successfully removed!");
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (param.getName().equals("id") && taskManager.getMapEpics().containsKey(id)) {
                                taskManager.deleteEpicTaskById(id);
                                sendText(exchange, "Task deleted ID - " + id);
                                return;
                            } else {
                                writeResponse(exchange,
                                        "Ошибка при удалении, не найден ID - " + id, 400);
                            }
                        }
                        break;
                    default:
                        writeResponse(exchange, "Incorrect request method", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void subTaskHandler(HttpExchange exchange) {
            try {
                String path = String.valueOf(exchange.getRequestURI());
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(path), String.valueOf(StandardCharsets.UTF_8));
                String requestMethod = exchange.getRequestMethod();
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                switch (requestMethod) {
                    case "GET":
                        if (params.isEmpty()) {
                            String response = gson.toJson(taskManager.getAllSubTask());
                            sendText(exchange, response);
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (exchange.getRequestURI().getPath().contains("epic")) {
                                String response = gson.toJson(taskManager.getSubTaskByEpic(id));
                                sendText(exchange, response);
                                return;
                            }
                            if (param.getName().equals("id") && taskManager.getMapSubTasks().containsKey(id)) {
                                String response = gson.toJson(taskManager.getSubTaskByID(id));
                                sendText(exchange, response);
                            } else {
                                writeResponse(exchange, "Empty Value is ID - " + id, 400);
                            }
                        }
                        break;
                    case "POST":
                        if (isJsonValid(body)) {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            Integer id = subtask.getId();
                            if (id == null) {
                                taskManager.createSubTask(subtask);
                                sendText(exchange, "Task successfully created, ID = " + subtask.getId());
                            } else {
                                taskManager.updateSubTask(subtask);
                                sendText(exchange, "Task successfully updated, ID = " + subtask.getId());
                            }
                        } else {
                            writeResponse(exchange, "Incorrect JSON received", 400);
                        }
                        break;
                    case "DELETE":
                        if (params.isEmpty()) {
                            taskManager.deleteAllSubs();
                            sendText(exchange, "All subtasks have been successfully deleted!");
                            return;
                        }
                        for (NameValuePair param : params) {
                            int id = Integer.parseInt(param.getValue());
                            if (param.getName().equals("id") && taskManager.getMapSubTasks().containsKey(id)) {
                                taskManager.deleteSubTaskById(id);
                                sendText(exchange, "Subtask removed ID - " + id);
                                return;
                            } else {
                                writeResponse(exchange,
                                        "Ошибка при удалении, не найден ID - " + id, 400);
                            }
                        }
                        break;
                    default:
                        writeResponse(exchange, "Incorrect request method", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void historyHandler(HttpExchange exchange) {
            try {
                String requestMethod = exchange.getRequestMethod();
                if (requestMethod.equals("GET")) {
                    String response = gson.toJson(taskManager.getHistory());
                    sendText(exchange, response);
                } else {
                    writeResponse(exchange, "Incorrect request method", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void allTasksHandler(HttpExchange exchange) {
            try {
                String requestMethod = exchange.getRequestMethod();
                if (requestMethod.equals("GET")) {
                    String response = gson.toJson(taskManager.getAllTasksByProject());
                    sendText(exchange, response);
                } else {
                    writeResponse(exchange, "Incorrect request method", 405);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private void sendText(HttpExchange h, String text) throws IOException {
            byte[] resp = text.getBytes("UTF-8");
            h.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        public boolean isJsonValid(String json) {
            try {
                gson.fromJson(json, Task.class);
                return true;
            } catch (com.google.gson.JsonSyntaxException exception) {
                return false;
            }
        }
    }


    public void start() {
        System.out.println("Started server " + PORT);
        System.out.println("http://localhost" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped " + PORT);
    }

}
