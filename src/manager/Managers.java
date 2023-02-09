package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.historyTaskManager.HistoryManager;
import manager.historyTaskManager.InMemoryHistoryManager;
import manager.httpTaskManager.HttpTaskManager;
import server.KVServer;
import server.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static KVServer getDefaultKVServer() throws IOException {
        return new KVServer();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
